package com.sksamuel.elastic4s.http

import cats.Show
import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.exts.Logging
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestClientBuilder.{HttpClientConfigCallback, RequestConfigCallback}

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

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

  // Executes the given request type T, and returns a Future of the response type U.
  def execute[T, U](request: T)(implicit exec: HttpExecutable[T, U]): Future[U] = {
    val p = Promise[U]()
    val f = exec.execute(client, request)
    f.onComplete {
      case Success(r) => p.tryComplete(exec.responseHandler.handle(r))
      case Failure(t) => p.tryFailure(t)
    }
    p.future
  }

  // executes the given request and returns a Future with the raw HttpResponse
  // this variant should be used if you need access to the underlying json and status code,
  // rather than a strongly typed object returned by the normal execute(req) method.
  def executeRaw[T](request: T)(implicit exec: HttpExecutable[T, _]): Future[HttpResponse] = exec.execute(client, request)

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

case class HttpResponse(statusCode: Int, entity: Option[HttpEntity], headers: Map[String, String])
case class HttpEntity(content: String, contentType: Option[String])
object HttpEntity {
  def apply(content: String, contentType: String): HttpEntity = HttpEntity(content, Some(contentType))
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
