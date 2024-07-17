package com.sksamuel.elastic4s

import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.duration.{Duration, _}
import scala.language.higherKinds

/**
  * An [[ElasticClient]] is used to execute HTTP requests against an ElasticSearch cluster.
  * This class delegates the actual HTTP calls to an instance of [[HttpClient]].
  *
  * Any third party HTTP client library can be made to work with elastic4s by creating an
  * instance of the HttpClient typeclass wrapping the underlying client library and
  * then creating the ElasticClient with it.
  *
  * @param client the HTTP client library to use
  **/
case class ElasticClient[F[_] : Executor : Functor](client: HttpClient[F]) {

  protected val logger: Logger = LoggerFactory.getLogger(getClass.getName)

  /**
    * Returns a String containing the request details.
    * The string will have the HTTP method, endpoint, params and if applicable the request body.
    */
  def show[T](t: T)(implicit handler: Handler[T, _]): String = Show[ElasticRequest].show(handler.build(t))

  // Executes the given request type T, and returns an effect of Response[U]
  // where U is particular to the request type.
  // For example a search request will return a Response[SearchResponse].
  def execute[T, U](t: T)(implicit handler: Handler[T, U], options: CommonRequestOptions): F[Response[U]] = {
    val request = handler.build(t)

    val request2 = if (options.timeout.toMillis > 0) {
      request.addParameter("timeout", options.timeout.toMillis + "ms")
    } else {
      request
    }

    val request3 = if (options.masterNodeTimeout.toMillis > 0) {
      request2.addParameter("master_timeout", options.masterNodeTimeout.toMillis + "ms")
    } else {
      request2
    }

    val request4 = options.headers.foldLeft(request3){ case (acc, (key, value)) => acc.addHeader(key, value) }

    val f = Executor[F].exec(client, request4)
    Functor[F].map(f) { resp =>
      handler.responseHandler.handle(resp) match {
        case Right(u) => RequestSuccess(resp.statusCode, resp.entity.map(_.content), resp.headers, u)
        case Left(error) => RequestFailure(resp.statusCode, resp.entity.map(_.content), resp.headers, error)
      }
    }
  }


  def close(): F[Unit] = client.close()
}

case class CommonRequestOptions(timeout: Duration, masterNodeTimeout: Duration, headers: Map[String, String] = Map.empty)

object CommonRequestOptions {
  implicit val defaults: CommonRequestOptions = CommonRequestOptions(0.seconds, 0.seconds, Map.empty)
}
