package com.sksamuel.elastic4s.http

import cats.Show
import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.exts.Logging
import org.apache.http.HttpHost
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestClientBuilder.{HttpClientConfigCallback, RequestConfigCallback}

import scala.concurrent.Future

trait HttpClient extends Logging {

  // returns the underlying java rest client
  def rest: RestClient

  // returns a String containing the Json of the request
  def show[T](request: T)(implicit show: Show[T]): String = show.show(request)

  // Executes the given request type T, and returns a Future of the response type U.
  def execute[T, U](request: T)(implicit exec: HttpExecutable[T, U]): Future[U] = exec.execute(rest, request)

  def close(): Unit
}

object HttpClient extends Logging {

  /**
    * Creates a new HttpClient from an existing java RestClient.
    * Use this method if you wish to customize the way the rest client is created.
    *
    * @param client the Java client to wrap
    * @return newly created Scala client
    */
  def fromRestClient(client: RestClient): HttpClient = new HttpClient {
    override def close(): Unit = rest.close()
    // returns the underlying java rest client
    override def rest: RestClient = client
  }

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

/**
  * RequestConfigCallback that performs a no-op on the given RequestConfig.Builder.
  *
  * Used as a default parameter to the HttpClient when no custom request
  * configuration is needed.
  *
  */
object NoOpRequestConfigCallback extends RequestConfigCallback {
  override def customizeRequestConfig(requestConfigBuilder: RequestConfig.Builder): RequestConfig.Builder = {
    requestConfigBuilder
  }
}

/**
  * HttpAsyncClientBuilder that performs a no-op on the given HttpAsyncClientBuilder
  *
  * Used as a default parameter to the HttpClient when no custom HttpAsync
  * configuration is needed.
  *
  */
object NoOpHttpClientConfigCallback extends HttpClientConfigCallback {
  override def customizeHttpClient(httpAsyncClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder = {
    httpAsyncClientBuilder
  }
}
