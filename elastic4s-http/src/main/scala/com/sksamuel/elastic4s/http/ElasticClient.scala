package com.sksamuel.elastic4s.http

import com.sksamuel.elastic4s.{ElasticsearchClientUri, Show}
import com.sksamuel.exts.Logging
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestClientBuilder.{HttpClientConfigCallback, RequestConfigCallback}

import scala.language.higherKinds

trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}

abstract class ElasticClient[F[_] : Functor : Executor] extends Logging {

  // the underlying client that performs the requests
  def client: HttpClient

  /**
    * Returns a json String containing the request body.
    * Note: This only works for requests which have a cats.Show typeclass implemented, which is most,
    * but not all. Also, some requests intentionally do not provide a Show implementation as
    * they are "header only" requests - that is, they have no body - for example, delete by id, or delete index.
    */
  def show[T](request: T)(implicit show: Show[T]): String = show.show(request)

  // Executes the given request type T, and returns an effect of Response[U]
  // where U is particular to the request type.
  // For example a search request will return a Response[SearchResponse].
  def execute[T, U](t: T)(implicit handler: Handler[T, U]): F[Response[U]] = {
    val request = handler.requestHandler(t)
    val f = implicitly[Executor[F]].exec(client, request)
    implicitly[Functor[F]].map(f) { resp =>
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
  def apply[F[_] : Functor : Executor](hc: HttpClient): ElasticClient[F] = new ElasticClient[F] {
    override def client: HttpClient = hc
    override def close(): Unit = hc.close()
  }

  /**
    * Creates a new ElasticClient from an existing Elasticsearch Java API RestClient.
    *
    * @param client the Java client to wrap
    * @return newly created Scala client
    */
  def fromRestClient[F[_] : Functor : Executor](client: RestClient): ElasticClient[F] =
    apply(new ElasticsearchJavaRestClient(client))

  /**
    * Creates a new ElasticClient using the elasticsearch Java API rest client
    * as the underlying client. Optional callbacks can be passed in to configure the client.
    *
    * Alternatively, create a RestClient manually and invoke fromRestClient(RestClient).
    */
  def apply[F[_] : Functor : Executor](uri: ElasticsearchClientUri,
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
