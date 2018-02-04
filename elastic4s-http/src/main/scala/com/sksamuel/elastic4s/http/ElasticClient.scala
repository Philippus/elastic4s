package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.exts.Logging
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestClientBuilder.{HttpClientConfigCallback, RequestConfigCallback}

import scala.language.higherKinds

abstract class ElasticClient extends Logging {

  // the underlying client that performs the requests
  def client: HttpClient

  /**
    * Returns a String containing the request details.
    * The string will have the HTTP method, endpoint, params and if applicable the request body.
    */
  def show[T](t: T)(implicit handler: Handler[T, _]): String = ElasticRequestShow.show(handler.requestHandler(t))

  // Executes the given request type T, and returns an effect of Response[U]
  // where U is particular to the request type.
  // For example a search request will return a Response[SearchResponse].
  def execute[T, U, F[_]](t: T)(implicit
                                functor: Functor[F],
                                executor: Executor[F],
                                handler: Handler[T, U],
                                manifest: Manifest[U]): F[Response[U]] = {
    val request = handler.requestHandler(t)
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
    * Creates a new ElasticClient by wrapping the given the HttpClient.
    *
    * Any library can be made to work with elastic4s by creating an instance
    * of the HttpClient typeclass wrapping the underlying library and
    * then creating the ElasticClient using this method.
    */
  def apply[F[_] : Functor : Executor](hc: HttpClient): ElasticClient = new ElasticClient {
    override def client: HttpClient = hc
    override def close(): Unit = hc.close()
  }

  /**
    * Creates a new ElasticClient from an existing Elasticsearch Java API RestClient.
    *
    * @param client the Java client to wrap
    * @return newly created Scala client
    */
  def fromRestClient[F[_] : Functor : Executor](client: RestClient): ElasticClient =
    apply(new ElasticsearchJavaRestClient(client))

  /**
    * Creates a new ElasticClient using the elasticsearch Java API rest client
    * as the underlying client. Optional callbacks can be passed in to configure the client.
    *
    * Alternatively, create a RestClient manually and invoke fromRestClient(RestClient).
    */
  def apply[F[_] : Functor : Executor](
                                        uri: ElasticsearchClientUri,
                                        requestConfigCallback: RequestConfigCallback = NoOpRequestConfigCallback,
                                        httpClientConfigCallback: HttpClientConfigCallback = NoOpHttpClientConfigCallback
                                      ): ElasticClient = {
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
