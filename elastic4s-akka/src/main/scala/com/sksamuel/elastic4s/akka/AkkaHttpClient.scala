package com.sksamuel.elastic4s.akka

import scala.concurrent.duration.Duration
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.stream.scaladsl.{FileIO, Keep, Sink, Source, StreamConverters}
import akka.stream.{ActorMaterializer, OverflowStrategy, QueueOfferResult}
import akka.util.ByteString
import com.sksamuel.elastic4s.http.HttpEntity.StringEntity
import com.sksamuel.elastic4s.http.{ElasticRequest, HttpClient => ElasticHttpClient, HttpEntity => ElasticHttpEntity, HttpResponse => ElasticHttpResponse}

class AkkaHttpClient(settings: AkkaHttpClientSettings)(implicit system: ActorSystem)
  extends ElasticHttpClient {

  import system.dispatcher

  private implicit val materializer = ActorMaterializer()

  private val http = Http()

  private val poolSettings = settings.poolSettings
    .withResponseEntitySubscriptionTimeout(Duration.Inf) // we guarantee to consume consume data from all responses

  private val poolFlow = http.superPool[Promise[ElasticHttpResponse]](
    settings = poolSettings
  )

  private val resolvedHosts: List[Uri] = {
    val scheme = if (settings.https) "https" else "http"
    settings.hosts.map(host => Uri.parseAbsolute(s"$scheme://$host")).toList
  }

  private val hostsFlow = Source.repeat(resolvedHosts).mapConcat(identity)

  private val queue =
    Source
      .queue[(ElasticRequest, Promise[ElasticHttpResponse])](settings.queueSize, OverflowStrategy.backpressure)
      .map { case (r, p) => (toRequest(r), p) }
      .zipWith(hostsFlow)((_, _))
      .map { case ((r, p), host) => (r.withUri(r.uri.resolvedAgainst(host)), p) }
      .via(poolFlow)
      .flatMapMerge(
        poolSettings.maxConnections, {
          case (Success(r), p) =>
            r.entity.dataBytes
              .fold(ByteString())(_ ++ _)
              .map(data => (Success(toResponse(r, data)), p))
              .recoverWithRetries(1, { // in case of TCP timeout or response subscription timeout, etc.
                case t: Throwable =>
                  Source.single(Failure(t), p)
              })
          case (Failure(e), p) => Source.single(Failure(e), p)
        }
      )
      .toMat(Sink.foreach({
        case (Success(resp), p) => p.success(resp)
        case (Failure(e), p) => p.failure(e)
      }))(Keep.left)
      .run()

  private def queueRequest(request: ElasticRequest): Future[ElasticHttpResponse] = {
    val responsePromise = Promise[ElasticHttpResponse]()
    queue.offer(request -> responsePromise).flatMap {
      case QueueOfferResult.Enqueued => responsePromise.future
      case QueueOfferResult.Dropped => Future.failed(new RuntimeException("Queue overflowed. Try again later."))
      case QueueOfferResult.Failure(ex) => Future.failed(ex)
      case QueueOfferResult.QueueClosed =>
        Future.failed(
          new RuntimeException("Queue was closed (pool shut down) while running the request. Try again later."))
    }
  }

  override def send(request: ElasticRequest, callback: Either[Throwable, ElasticHttpResponse] => Unit): Unit = {
    queueRequest(request).onComplete {
      case Success(r) => callback(Right(r))
      case Failure(e) => callback(Left(e))
    }
  }

  def shutdown(): Future[Unit] = {
    http.shutdownAllConnectionPools()
  }

  override def close(): Unit = {
    shutdown()
  }

  private def toRequest(request: ElasticRequest): HttpRequest = {
    HttpRequest(
      method = HttpMethod.custom(request.method),
      uri = Uri(request.endpoint).withQuery(Query(request.params.mapValues(_.toString))),
      entity = request.entity.map(toEntity).getOrElse(HttpEntity.Empty)
    )
  }

  private def toResponse(response: HttpResponse, data: ByteString): ElasticHttpResponse = {
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
          contentType.flatMap(value => ContentType.parse(value).right.toOption).getOrElse(ContentTypes.`text/plain(UTF-8)`)
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

  def apply(settings: AkkaHttpClientSettings)(implicit system: ActorSystem): AkkaHttpClient = {
    new AkkaHttpClient(settings)
  }
}
