package com.sksamuel.elastic4s.akka

import akka.actor.ActorSystem
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.stream.scaladsl.{FileIO, Keep, Sink, Source, StreamConverters}
import akka.stream.{Materializer, OverflowStrategy, QueueOfferResult}
import akka.util.ByteString
import com.sksamuel.elastic4s.HttpEntity.StringEntity
import com.sksamuel.elastic4s.{
  ElasticRequest,
  HttpClient => ElasticHttpClient,
  HttpEntity => ElasticHttpEntity,
  HttpResponse => ElasticHttpResponse
}

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}

class AkkaHttpClient private[akka] (
  settings: AkkaHttpClientSettings,
  blacklist: Blacklist,
  httpPoolFactory: HttpPoolFactory
)(implicit system: ActorSystem)
    extends ElasticHttpClient {

  import AkkaHttpClient._
  import system.dispatcher

  private implicit val materializer: Materializer = Materializer(system)

  private val scheme = if (settings.https) "https" else "http"

  private val queue =
    Source
      .queue[(ElasticRequest, RequestState)](
        settings.queueSize,
        OverflowStrategy.backpressure
      )
      .statefulMapConcat { () =>
        val hosts = iterateHosts

        in =>
          {
            (in, hosts.next()) match {
              case ((r, s), Some(host)) =>
                // if host is resolved - send request forward
                toRequest(r, host) match {
                  case Success(req) =>
                    s.host.success(host)
                    (req, s) :: Nil
                  case Failure(e) =>
                    s.host.failure(e)
                    s.response.failure(e)
                    Nil
                }
              case ((_, s), None) =>
                // if not - all hosts are blacklisted, return an error
                val exception = AllHostsBlacklistedException
                s.host.failure(exception)
                s.response.failure(exception)
                Nil
            }
          }
      }
      .via(httpPoolFactory.create[RequestState]())
      .flatMapMerge(
        settings.poolSettings.maxConnections, {
          case (request, Success(response), state)
              if request.method == HttpMethods.HEAD =>
            response.discardEntityBytes()
            Source.single(
              (Success(toResponse(response, ByteString.empty)), state)
            )
          case (_, Success(r), s) =>
            r.entity.dataBytes
              .fold(ByteString())(_ ++ _)
              .map(data => (Success(toResponse(r, data)), s))
              .recoverWithRetries(1, { // in case of TCP timeout or response subscription timeout, etc.
                case t: Throwable =>
                  Source.single(Failure(t), s)
              })
          case (_, Failure(e), s) => Source.single(Failure(e), s)
        }
      )
      .toMat(Sink.foreach({
        case (Success(resp), s) => s.response.success(resp)
        case (Failure(e), s)    => s.response.failure(e)
      }))(Keep.left)
      .run()

  /**
    * Iterator of Some(host) or None if all hosts are blacklisted.
    */
  private def iterateHosts: Iterator[Option[String]] =
    Iterator
      .continually(settings.hosts)
      .flatten
      .flatMap { host =>
        if (blacklist.contains(host)) {
          logger.trace(s"[$host] is in blacklist")
          if (blacklist.size < settings.hosts.size) Nil
          else None :: Nil
        } else Some(host) :: Nil
      }

  private def queueRequest(request: ElasticRequest,
                           state: RequestState): Future[ElasticHttpResponse] = {
    queue.offer(request -> state).flatMap {
      case QueueOfferResult.Enqueued => state.response.future
      case QueueOfferResult.Dropped =>
        Future.failed(new Exception("Queue overflowed. Try again later."))
      case QueueOfferResult.Failure(ex) => Future.failed(ex)
      case QueueOfferResult.QueueClosed =>
        Future.failed(
          new Exception(
            "Queue was closed (pool shut down) while running the request. Try again later."
          )
        )
    }
  }

  private def queueRequestWithRetry(
    request: ElasticRequest,
    startTimeNanos: Long = System.nanoTime
  ): Future[ElasticHttpResponse] = {

    val state = RequestState()

    def retryIfPossible(
      notPossible: => Either[Throwable, ElasticHttpResponse]
    ): Future[ElasticHttpResponse] = {
      val timePassed = System.nanoTime - startTimeNanos
      if (timePassed < settings.maxRetryTimeout.toNanos) {
        logger.trace(s"Retrying a request: ${request.endpoint}")
        queueRequestWithRetry(request, startTimeNanos)
      } else {
        notPossible match {
          case Left(exc) =>
            Future.failed(
              new Exception(
                s"Request retries exceeded max retry timeout [${settings.maxRetryTimeout}]",
                exc
              )
            )
          case Right(resp) =>
            Future.successful(resp)
        }
      }
    }

    def markDead(): Future[Unit] = {
      state.host.future
        .map { host =>
          if (blacklist.add(host)) {
            logger.debug(s"added [$host] to blacklist")
          } else {
            logger.trace(s"updated [$host] in a blacklist")
          }
        }
    }

    def markAlive(): Future[Unit] = {
      state.host.future
        .map { host =>
          if (blacklist.remove(host)) {
            logger.debug(s"removed [$host] from blacklist")
          }
        }
    }

    queueRequest(request, state)
      .flatMap { response =>
        val status = StatusCode.int2StatusCode(response.statusCode)
        if (status.isSuccess()) {
          markAlive().map(_ => response)
        } else {
          if (isRetryStatus(status)) {
            markDead().flatMap(_ => retryIfPossible(Right(response)))
          } else {
            // mark host alive and don't retry, as the error should be a request problem
            markAlive().map(_ => response)
          }
        }
      }
      .recoverWith {
        case err @ AllHostsBlacklistedException => retryIfPossible(Left(err))
        case err: Throwable =>
          markDead().flatMap(_ => retryIfPossible(Left(err)))
      }
  }

  private def isRetryStatus(statusCode: StatusCode) = {
    statusCode match {
      case StatusCodes.BadGateway         => true
      case StatusCodes.ServiceUnavailable => true
      case StatusCodes.GatewayTimeout     => true
      case _                              => false
    }
  }

  private[akka] def sendAsync(
    request: ElasticRequest
  ): Future[ElasticHttpResponse] = {
    queueRequestWithRetry(request)
  }

  override def send(
    request: ElasticRequest,
    callback: Either[Throwable, ElasticHttpResponse] => Unit
  ): Unit = {
    sendAsync(request).onComplete {
      case Success(r) => callback(Right(r))
      case Failure(e) => callback(Left(e))
    }
  }

  def shutdown(): Future[Unit] = {
    httpPoolFactory.shutdown()
  }

  override def close(): Unit = {
    shutdown()
  }

  private def toRequest(request: ElasticRequest,
                        host: String): Try[HttpRequest] = Try {
    val httpRequest = HttpRequest(
      method = HttpMethods
        .getForKeyCaseInsensitive(request.method)
        .getOrElse(HttpMethod.custom(request.method)),
      uri = Uri(request.endpoint)
        .withQuery(Query(request.params))
        .withAuthority(Uri.Authority.parse(host))
        .withScheme(scheme),
      entity = request.entity.map(toEntity).getOrElse(HttpEntity.Empty)
    )

    settings.requestCallback(
      if (settings.hasCredentialsDefined) {
        httpRequest.addCredentials(BasicHttpCredentials(settings.username.get, settings.password.get))
      } else {
        httpRequest
      }
    )
  }

  private def toResponse(response: HttpResponse,
                         data: ByteString): ElasticHttpResponse = {
    ElasticHttpResponse(
      response.status.intValue(),
      Some(StringEntity(data.utf8String, None)),
      response.headers.map(h => h.name -> h.value).toMap
    )
  }

  private def toEntity(entity: ElasticHttpEntity): RequestEntity = {
    entity match {
      case ElasticHttpEntity.StringEntity(content, contentType) =>
        val ct =
          contentType
            .flatMap(value => ContentType.parse(value).right.toOption)
            .getOrElse(ContentTypes.`text/plain(UTF-8)`)
        HttpEntity(ct, ByteString(content))
      case ElasticHttpEntity.ByteArrayEntity(content, contentType) =>
        val ct =
          contentType
            .flatMap(value => ContentType.parse(value).right.toOption)
            .getOrElse(ContentTypes.`text/plain(UTF-8)`)
        HttpEntity(ct, ByteString(content))
      case ElasticHttpEntity.FileEntity(file, contentType) =>
        val ct = contentType
          .flatMap(value => ContentType.parse(value).right.toOption)
          .getOrElse(ContentTypes.`application/octet-stream`)
        HttpEntity(ct, file.length, FileIO.fromPath(file.toPath))
      case ElasticHttpEntity.InputStreamEntity(stream, contentType) =>
        val ct = contentType
          .flatMap(value => ContentType.parse(value).right.toOption)
          .getOrElse(ContentTypes.`application/octet-stream`)
        HttpEntity(ct, StreamConverters.fromInputStream(() => stream))
    }
  }
}

object AkkaHttpClient {

  def apply(
    settings: AkkaHttpClientSettings
  )(implicit system: ActorSystem): AkkaHttpClient = {

    val blacklist = new DefaultBlacklist(
      settings.blacklistMinDuration,
      settings.blacklistMaxDuration
    )

    val httpPoolFactory = new DefaultHttpPoolFactory(settings.poolSettings)

    new AkkaHttpClient(settings, blacklist, httpPoolFactory)
  }

  private[akka] case class RequestState(response: Promise[ElasticHttpResponse] =
                                          Promise(),
                                        host: Promise[String] = Promise())

  private[akka] case object AllHostsBlacklistedException
      extends Exception("All hosts are blacklisted!")

}
