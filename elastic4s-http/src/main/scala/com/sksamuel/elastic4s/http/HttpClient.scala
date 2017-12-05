package com.sksamuel.elastic4s.http

import java.io.{File, InputStream}

import cats.Show
import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.update.ElasticError
import com.sksamuel.exts.Logging
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestClientBuilder.{HttpClientConfigCallback, RequestConfigCallback}

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

trait Response[+U] {
  def status: Int // the http status code of the response
  def body: Option[String] // the http response body if the response included one
  def headers: Map[String, String] // any http headers included in the response
  def get: U // returns the marshalled response U or throws an exception
  def error: ElasticError // returns the error or throw an exception
  def isError: Boolean // returns true if this is an error response
  final def isSuccess: Boolean  = !isError // returns true if this is a success
  final def map[V](f: U => V): Option[V] = if (isError) None else Some(f(get))
  final def fold[V](ifError: => V)(f: U => V): V = if (isError) ifError else f(get)
  final def foreach[V](f: U => V) = if (!isError) f(get)
}

case class RequestSuccess[U](override val status: Int, // the http status code of the response
                             override val body: Option[String], // the http response body if the response included one
                             override val headers: Map[String, String], // any http headers included in the response
                             u: U) extends Response[U] {
  override def get = u
  override def isError = false
  override def error = throw new NoSuchElementException(s"Request success $u")
}

case class RequestFailure(override val status: Int, // the http status code of the response
                          override val body: Option[String], // the http response body if the response included one
                          override val headers: Map[String, String], // any http headers included in the response
                          override val error: ElasticError) extends Response[Nothing] {
  override def get = throw new NoSuchElementException(s"Request Failure $error")
  override def isError = true
}

trait HttpClient extends Logging {

  import scala.concurrent.ExecutionContext.Implicits.global

  // the underlying client that performs the requests
  def client: HttpRequestClient

  /**
    * Returns a json String containing the request body.
    * Note: This only works for requests which have a cats.Show typeclass implemented, which is most,
    * but not all. Also, some requests intentionally do not provide a Show implementation as
    * they are "header only" requests - that is, they have no body - for example, delete by id, or delete index.
    */
  def show[T](request: T)(implicit show: Show[T]): String = show.show(request)

  // Executes the given request type T, and returns a Future of Response[U] where U is particular to the request type.
  // For example a search request will return a Future[Response[SearchResponse]].
  // The returned Response is an ADT
  def execute[T, U](request: T)(implicit exec: HttpExecutable[T, U]): Future[Response[U]] = {
    val p = Promise[Response[U]]()
    val f = exec.execute(client, request)
    f.onComplete {
      case Success(r) =>
        try exec.responseHandler.handle(r) match {
          case Right(u) =>
            val resp = RequestSuccess(r.statusCode, r.entity.map(_.content), r.headers, u)
            p.trySuccess(resp)
          case Left(error) =>
            val resp = RequestFailure(r.statusCode, r.entity.map(_.content), r.headers, error)
            p.trySuccess(resp)
        } catch {
          case t: Throwable => p.tryFailure(t)
        }
      case Failure(t) => p.tryFailure(t)
    }
    p.future
  }

  def close(): Unit
}

/**
  * Adapts an underlying http client so that it can be used by the elastic4s http client.
  *
  * Implementations should return a HttpResponse for all requests that received a response, including errors
  * like 500s or not founds like 404s.
  *
  * A failed future should only be returned if the communication itself failed.
  */
trait HttpRequestClient extends Logging {

  def async(method: String, endpoint: String): Future[HttpResponse] = async(method, endpoint, Map.empty)

  def async(method: String,
            endpoint: String,
            params: Map[String, Any]): Future[HttpResponse]

  def async(method: String,
            endpoint: String,
            params: Map[String, Any],
            entity: HttpEntity): Future[HttpResponse]

  def close(): Unit
}

case class HttpResponse(statusCode: Int, entity: Option[HttpEntity.StringEntity], headers: Map[String, String])
sealed trait HttpEntity
object HttpEntity {
  def apply(content: String): HttpEntity = HttpEntity(content, "application/json; charset=utf-8")
  def apply(content: String, contentType: String): HttpEntity = StringEntity(content, Some(contentType))

  case class StringEntity(content: String, contentType: Option[String]) extends HttpEntity
  case class InputStreamEntity(content: InputStream, contentType: Option[String]) extends HttpEntity
  case class FileEntity(content: File, contentType: Option[String]) extends HttpEntity
}

object HttpClient extends Logging {

  /**
    * Creates a new HttpClient by wrapping the given the HttpRequestClient.
    *
    * Any underlying library can be made to work with elastic4s.HttpClient by creating an instance
    * of the HttpRequestClient typeclass.
    */
  def apply(hrc: HttpRequestClient): HttpClient = new HttpClient {
    override def client: HttpRequestClient = hrc
    override def close(): Unit = hrc.close()
  }

  /**
    * Creates a new HttpClient from an existing Elasticsearch Java API RestClient.
    *
    * @param client the Java client to wrap
    * @return newly created Scala client
    */
  def fromRestClient(client: RestClient): HttpClient = apply(new ElasticsearchJavaRestClient(client))

  /**
    * Creates a new HttpClient using the elasticsearch Java API rest client as the underlying
    * request client. Optional callbacks can be passed in to configure the client.
    *
    * Alternatively, create a RestClient manually and call apply(RestClient).
    */
  def apply(uri: ElasticsearchClientUri,
            requestConfigCallback: RequestConfigCallback = NoOpRequestConfigCallback,
            httpClientConfigCallback: HttpClientConfigCallback = NoOpHttpClientConfigCallback
           ): HttpClient = {
    val hosts = uri.hosts.map { case (host, port) =>
      new HttpHost(host, port, if (uri.options.getOrElse("ssl", "false") == "true") "https" else "http")
    }
    logger.info(s"Creating HTTP client on ${hosts.mkString(",")}")

    val client = RestClient.builder(hosts: _*)
      .setRequestConfigCallback(requestConfigCallback)
      .setHttpClientConfigCallback(httpClientConfigCallback)
      .build()

    HttpClient.fromRestClient(client)
  }
}
