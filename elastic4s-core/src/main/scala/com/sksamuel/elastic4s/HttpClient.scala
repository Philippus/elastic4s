package com.sksamuel.elastic4s

import org.slf4j.{Logger, LoggerFactory}

import scala.language.higherKinds

/** A typeclass that an underlying http client can implement, so that it can be used by the [[ElasticClient]]
  * implementation by elastic4s.
  *
  * In other words, this is a wrapper trait so that HTTP libraries such as Apache HTTP commons, Akka HTTP client, STTP
  * or whatever can be used with elasticsearch. The wrapped client can then be passed into the ElasticClient.
  */
trait HttpClient[F[_]] {

  protected val logger: Logger = LoggerFactory.getLogger(getClass.getName)

  /** Sends the given request to elasticsearch.
    *
    * Implementations should invoke the callback function once the response is known.
    *
    * The callback function should be invoked with a HttpResponse for all requests that received a response, including
    * 4xx and 5xx responses. The callback function should only be invoked with an exception if the client could not
    * complete the request.
    */
  def send(request: ElasticRequest): F[HttpResponse]

  /** Closes the underlying http client. Can be a no-op if the underlying client does not have state that needs to be
    * closed.
    */
  def close(): F[Unit]
}
