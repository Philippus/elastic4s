package com.sksamuel.elastic4s.http

import java.util.concurrent.Executor

import cats.{Invariant, Show}
import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.exts.Logging
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestClientBuilder.{HttpClientConfigCallback, RequestConfigCallback}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

sealed trait Response[+U] {
  def status: Int // the http status code of the response
  def body: Option[String] // the http response body if the response included one
  def headers: Map[String, String] // any http headers included in the response
  def result: U // returns the marshalled response U or throws an exception
  def error: ElasticError // returns the error or throw an exception
  def isError: Boolean // returns true if this is an error response
  final def isSuccess: Boolean = !isError // returns true if this is a success
  final def map[V](f: U => V): Option[V] = if (isError) None else Some(f(result))
  final def fold[V](ifError: => V)(f: U => V): V = if (isError) ifError else f(result)
  final def foreach[V](f: U => V): Unit = if (!isError) f(result)
}

case class RequestSuccess[U](override val status: Int, // the http status code of the response
                             override val body: Option[String], // the http response body if the response included one
                             override val headers: Map[String, String], // any http headers included in the response
                             override val result: U)
  extends Response[U] {
  override def isError = false
  override def error = throw new NoSuchElementException(s"Request success $result")
}

case class RequestFailure(override val status: Int, // the http status code of the response
                          override val body: Option[String], // the http response body if the response included one
                          override val headers: Map[String, String], // any http headers included in the response
                          override val error: ElasticError)
  extends Response[Nothing] {
  override def result = throw new NoSuchElementException(s"Request Failure $error")
  override def isError = true
}

trait Show[-T] extends Serializable {
  def show(t: T): String
}

trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}

trait ElasticClient[F[_] : Functor] extends Logging {

  // the underlying client that performs the requests
  def client: HttpClient

  /**
    * Returns a json String containing the request body.
    * Note: This only works for requests which have a cats.Show typeclass implemented, which is most,
    * but not all. Also, some requests intentionally do not provide a Show implementation as
    * they are "header only" requests - that is, they have no body - for example, delete by id, or delete index.
    */
  def show[T](request: T)(implicit show: Show[T]): String = show.show(request)

  // Executes the given request T, and returns an effect of Response[U] where U is particular to the request type.
  // For example a search request will return a Response[SearchResponse].
  def execute[T, U](request: T)(
    implicit exec: HttpExecutable[T, U],
    executionContext: ExecutionContext = ExecutionContext.Implicits.global
  ): F[Either[RequestFailure, RequestSuccess[U]]] =
    exec
      .execute(client, request)
      .map(
        r =>
          exec.responseHandler.handle(r) match {
            case Right(u) =>
              Right(RequestSuccess(r.statusCode, r.entity.map(_.content), r.headers, u))
            case Left(error) =>
              Left(RequestFailure(r.statusCode, r.entity.map(_.content), r.headers, error))
          }
      )

  def close(): Unit
}

// models everything needed to send the request to elasticsearch
// all request types, like SearchRequest, UpdateRequest
case class ElasticRequest(method: String, url: String, headers: Map[String, String], body: Option[HttpEntity])

trait Executor[F[_]] {
  def exec(request: ElasticRequest)
}

object ElasticClient extends Logging {

  /**
    * Creates a new ElasticClient by wrapping the given the HttpClient.
    *
    * Any library can be made to work with elastic4s by creating an instance
    * of the HttpClient typeclass wrapping the underlying library and
    * then creating the ElasticClient using this method.
    */
  def apply[F[_] : Executor[F]](hc: HttpClient): ElasticClient[F] = new ElasticClient[F] {
    override def client: HttpClient = hc
    override def close(): Unit = hc.close()
  }

  /**
    * Creates a new ElasticClient from an existing Elasticsearch Java API RestClient.
    *
    * @param client the Java client to wrap
    * @return newly created Scala client
    */
  def fromRestClient[F[_] : Executor[F]](client: RestClient): ElasticClient[F] =
    apply(new ElasticsearchJavaRestClient(client))

  /**
    * Creates a new ElasticClient using the elasticsearch Java API rest client
    * as the underlying client. Optional callbacks can be passed in to configure the client.
    *
    * Alternatively, create a RestClient manually and invoke fromRestClient(RestClient).
    */
  def apply[F[_] : Executor[F]](uri: ElasticsearchClientUri,
                                requestConfigCallback: RequestConfigCallback = NoOpRequestConfigCallback,
                                httpClientConfigCallback: HttpClientConfigCallback = NoOpHttpClientConfigCallback): ElasticClient[F] = {
    val hosts = uri.hosts.map {
      case (host, port) =>
        new HttpHost(host, port, if (uri.options.getOrElse("ssl", "false") == "true") "https" else "http")
    }
    logger.info(s"Creating HTTP client on ${hosts.mkString(",")}")

    val client = RestClient
      .builder(hosts: _*)
      .setRequestConfigCallback(requestConfigCallback)
      .setHttpClientConfigCallback(httpClientConfigCallback)
      .build()

    ElasticClient.fromRestClient(client)
  }
}
