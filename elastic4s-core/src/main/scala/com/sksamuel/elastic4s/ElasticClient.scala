package com.sksamuel.elastic4s

import com.sksamuel.exts.Logging

import scala.language.higherKinds

abstract class ElasticClient extends Logging {

  // the underlying client that performs the requests
  def client: HttpClient

  /**
    * Returns a String containing the request details.
    * The string will have the HTTP method, endpoint, params and if applicable the request body.
    */
  def show[T](t: T)(implicit handler: Handler[T, _]): String = Show[ElasticRequest].show(handler.build(t))

  // Executes the given request type T, and returns an effect of Response[U]
  // where U is particular to the request type.
  // For example a search request will return a Response[SearchResponse].
  def execute[T, U, F[_]](t: T)(implicit
                                functor: Functor[F],
                                executor: Executor[F],
                                handler: Handler[T, U],
                                manifest: Manifest[U]): F[Response[U]] = {
    val request = handler.build(t)
    val f = executor.exec(client, request)
    functor.map(f) { resp =>
      handler.responseHandler.handle(resp) match {
        case Right(u) => RequestSuccess(resp.statusCode, resp.entity.map(_.content), resp.headers, u)
        case Left(error) => RequestFailure(resp.statusCode, resp.entity.map(_.content), resp.headers, error)
      }
    }
  }

  def close(): Unit
}

object ElasticClient extends Logging {

  /**
    * Creates a new [[ElasticClient]] by wrapping the given the [[HttpClient]].
    *
    * Any library can be made to work with elastic4s by creating an instance
    * of the HttpClient typeclass wrapping the underlying library and
    * then creating the ElasticClient using this method.
    */
  def apply[F[_] : Functor : Executor](hc: HttpClient): ElasticClient = new ElasticClient {
    override def client: HttpClient = hc
    override def close(): Unit = hc.close()
  }
}
