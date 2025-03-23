package com.sksamuel.elastic4s

import cats.Functor
import cats.syntax.functor._
import org.slf4j.{Logger, LoggerFactory}

import java.util.Base64
import scala.concurrent.duration._
import scala.language.higherKinds

/** An [[ElasticClient]] is used to execute HTTP requests against an ElasticSearch cluster. This class delegates the
  * actual HTTP calls to an instance of [[HttpClient]].
  *
  * Any third party HTTP client library can be made to work with elastic4s by creating an instance of the HttpClient
  * typeclass wrapping the underlying client library and then creating the ElasticClient with it.
  *
  * @param client
  *   the HTTP client library to use
  */
case class ElasticClient[F[_]: Functor](client: HttpClient[F]) {

  protected val logger: Logger = LoggerFactory.getLogger(getClass.getName)

  /** Returns a String containing the request details. The string will have the HTTP method, endpoint, params and if
    * applicable the request body.
    */
  def show[T](t: T)(implicit handler: Handler[T, _]): String = Show[ElasticRequest].show(handler.build(t))

  // Executes the given request type T, and returns an effect of Response[U]
  // where U is particular to the request type.
  // For example a search request will return a Response[SearchResponse].
  def execute[T, U](t: T)(implicit handler: Handler[T, U], options: CommonRequestOptions): F[Response[U]] = {
    val request = handler.build(t)

    val request2 = if (options.timeout.toMillis > 0) {
      request.addParameter("timeout", s"${options.timeout.toMillis}ms")
    } else {
      request
    }

    val request3 = if (options.masterNodeTimeout.toMillis > 0) {
      request2.addParameter("master_timeout", s"${options.masterNodeTimeout.toMillis}ms")
    } else {
      request2
    }

    val request4 = request3.addHeaders(options.headers)

    val request5 = authenticate(request4, options.authentication)

    client.send(request5).map { resp =>
      handler.responseHandler.handle(resp) match {
        case Right(u)    => RequestSuccess(resp.statusCode, resp.entity.map(_.content), resp.headers, u)
        case Left(error) => RequestFailure(resp.statusCode, resp.entity.map(_.content), resp.headers, error)
      }
    }
  }

  private def authenticate(request: ElasticRequest, authentication: Authentication): ElasticRequest = {
    authentication match {
      case Authentication.UsernamePassword(username, password) =>
        request.addHeader(
          "Authorization",
          "Basic " + Base64.getEncoder.encodeToString(s"$username:$password".getBytes)
        )
      case Authentication.ApiKey(apiKey)                       =>
        request.addHeader(
          "Authorization",
          "ApiKey " + Base64.getEncoder.encodeToString(apiKey.getBytes)
        )
      case Authentication.NoAuth                               =>
        request
    }
  }

  def close(): F[Unit] = client.close()
}

sealed trait Authentication

object Authentication {
  case class UsernamePassword(username: String, password: String) extends Authentication

  case class ApiKey(apiKey: String) extends Authentication

  case object NoAuth extends Authentication
}

case class CommonRequestOptions(
    timeout: Duration,
    masterNodeTimeout: Duration,
    headers: Map[String, String] = Map.empty,
    authentication: Authentication = Authentication.NoAuth
)

object CommonRequestOptions {
  implicit val defaults: CommonRequestOptions = CommonRequestOptions(
    timeout = 0.seconds,
    masterNodeTimeout = 0.seconds,
    headers = Map.empty,
    authentication = Authentication.NoAuth
  )
}
