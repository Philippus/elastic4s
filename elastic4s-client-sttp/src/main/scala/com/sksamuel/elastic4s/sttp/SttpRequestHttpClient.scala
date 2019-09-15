package com.sksamuel.elastic4s.sttp

import java.io._
import java.nio.file.Files

import com.sksamuel.elastic4s.HttpEntity.{ByteArrayEntity, FileEntity, InputStreamEntity, StringEntity}
import com.sksamuel.elastic4s.{ElasticNodeEndpoint, ElasticRequest, ElasticsearchClientUri, HttpClient, HttpEntity, HttpResponse}
import com.sksamuel.exts.OptionImplicits._
import com.softwaremill.sttp._
import com.softwaremill.sttp.asynchttpclient.future.AsyncHttpClientFutureBackend

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scala.io.Source
import scala.util.{Failure, Success}

class SttpRequestHttpClient(nodeEndpoint: ElasticNodeEndpoint)(
  implicit ec: ExecutionContext, sttpBackend: SttpBackend[Future, Nothing]) extends HttpClient {

  /** Alternative constructor for backwards compatibility. */
  @deprecated("Use the constructor which takes an ElasticNodeEndpoint", "7.3.2")
  def this(clientUri: ElasticsearchClientUri) {
    this(ElasticNodeEndpoint("http", clientUri.hosts.head._1, clientUri.hosts.head._2, None))(
      SttpRequestHttpClient.defaultEc, SttpRequestHttpClient.defaultSttpBackend)
  }

  private def request(method: String, endpoint: String, params: Map[String, Any]): Request[String, Nothing] = {
    val url = uri"${nodeEndpoint.protocol}://${nodeEndpoint.host}:${nodeEndpoint.port}/$endpoint?$params"
    method.toUpperCase match {
      case "GET"    => sttp.get(url)
      case "HEAD"   => sttp.head(url)
      case "POST"   => sttp.post(url)
      case "PUT"    => sttp.put(url)
      case "DELETE" => sttp.delete(url)
    }
  }

  private def processResponse(resp: Response[String]): HttpResponse = {
    val entity = resp.body match {
      case Left(e)     => None
      case Right(body) => HttpEntity.StringEntity(body, resp.contentType).some
    }
    HttpResponse(resp.code, entity, resp.headers.toMap)
  }

  def async(method: String, endpoint: String, params: Map[String, Any]): Request[String, Nothing] =
    request(method, endpoint, params)

  def async(method: String,
            endpoint: String,
            params: Map[String, Any],
            entity: HttpEntity): Request[String, Nothing] = {
    val r  = request(method, endpoint, params)
    val r2 = entity.contentCharset.fold(r)(r.contentType)
    entity match {
      case StringEntity(content: String, _) => r2.body(content)
      case ByteArrayEntity(content, _) => r2.body(content)
      case InputStreamEntity(in: InputStream, _) =>
        r2.body(Source.fromInputStream(in, "UTF8").getLines().mkString("\n"))
      case FileEntity(file: File, _) => r2.body(Files.readAllBytes(file.toPath))
    }
  }

  override def close(): Unit = sttpBackend.close()

  /**
    * Sends the given request to elasticsearch.
    *
    * Implementations should invoke the callback function once the response is known.
    *
    * The callback function should be invoked with a HttpResponse for all requests that received
    * a response, including 4xx and 5xx responses. The callback function should only be invoked
    * with an exception if the client failed.
    */
  override def send(request: ElasticRequest, callback: Either[Throwable, HttpResponse] => Unit): Unit = {
    val f = request.entity match {
      case Some(entity) => async(request.method, request.endpoint, request.params, entity).send()
      case None         => async(request.method, request.endpoint, request.params).send()
    }
    f.onComplete {
      case Success(resp) => callback(Right(processResponse(resp)))
      case Failure(t)    => callback(Left(t))
    }
  }
}

object SttpRequestHttpClient {

  private def defaultEc: ExecutionContext = ExecutionContext.global
  private def defaultSttpBackend: SttpBackend[Future, Nothing] = AsyncHttpClientFutureBackend()

  /** Instantiate an [[SttpRequestHttpClient]] with reasonable defaults for the implicit parameters. */
  def apply(nodeEndpoint: ElasticNodeEndpoint): SttpRequestHttpClient = new SttpRequestHttpClient(nodeEndpoint)(
    defaultEc, defaultSttpBackend)

}
