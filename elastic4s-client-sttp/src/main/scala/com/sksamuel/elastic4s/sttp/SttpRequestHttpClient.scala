package com.sksamuel.elastic4s.sttp

import java.io._
import java.nio.file.Files
import com.sksamuel.elastic4s.HttpEntity.{ByteArrayEntity, FileEntity, InputStreamEntity, StringEntity}
import com.sksamuel.elastic4s.{
  ElasticNodeEndpoint,
  ElasticRequest,
  ElasticsearchClientUri,
  HttpClient,
  HttpEntity,
  HttpResponse
}
import com.sksamuel.elastic4s.ext.OptionImplicits._
import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import sttp.client3._
import sttp.model.Uri
import sttp.model.Uri.{PathSegments, QuerySegment}
import sttp.monad.{FutureMonad, MonadError}
import sttp.monad.syntax.MonadErrorOps

import scala.language.higherKinds

class SttpRequestHttpClient[F[_]: MonadError](
    nodeEndpoint: ElasticNodeEndpoint
)(
    implicit sttpBackend: SttpBackend[F, Any]
) extends HttpClient[F] {

  private def request(
      method: String,
      endpoint: String,
      params: Map[String, Any],
      headers: Map[String, String]
  ): Request[String, Any] = {
    val url = new Uri(
      scheme = Some(nodeEndpoint.protocol),
      authority = None,
      pathSegments = PathSegments.absoluteOrEmptyS(collection.immutable.Seq(endpoint.stripPrefix("/").split('/'): _*)),
      querySegments =
        collection.immutable.Seq(params.map { case (k, v) => QuerySegment.KeyValue(k, v.toString) }.toSeq: _*),
      fragmentSegment = None
    ).host(nodeEndpoint.host).port(nodeEndpoint.port)
    val req = method.toUpperCase match {
      case "GET"    => quickRequest.get(url)
      case "HEAD"   => quickRequest.head(url)
      case "POST"   => quickRequest.post(url)
      case "PUT"    => quickRequest.put(url)
      case "DELETE" => quickRequest.delete(url)
    }
    req.headers(headers)
  }

  private def processResponse(resp: Response[String]): HttpResponse = {
    val strToHttpEntity = (body: String) => HttpEntity.StringEntity(body, resp.contentType).some
    val entity          = strToHttpEntity(resp.body)

    HttpResponse(resp.code.code, entity, resp.headers.map(h => h.name -> h.value).toMap)
  }

  def async(
      method: String,
      endpoint: String,
      params: Map[String, Any],
      headers: Map[String, String]
  ): Request[String, Any] =
    request(method, endpoint, params, headers)

  def async(
      method: String,
      endpoint: String,
      params: Map[String, Any],
      headers: Map[String, String],
      entity: HttpEntity
  ): Request[String, Any] = {
    val r  = request(method, endpoint, params, headers)
    val r2 = entity.contentCharset.fold(r)(r.contentType)
    entity match {
      case StringEntity(content: String, _)      => r2.body(content)
      case ByteArrayEntity(content, _)           => r2.body(content)
      case InputStreamEntity(in: InputStream, _) =>
        r2.body(Source.fromInputStream(in, "UTF8").getLines().mkString("\n"))
      case FileEntity(file: File, _)             => r2.body(Files.readAllBytes(file.toPath))
    }
  }

  override def close(): F[Unit] = sttpBackend.close()

  /** Sends the given request to elasticsearch.
    *
    * Implementations should invoke the callback function once the response is known.
    *
    * The callback function should be invoked with a HttpResponse for all requests that received a response, including
    * 4xx and 5xx responses. The callback function should only be invoked with an exception if the client failed.
    */
  override def send(request: ElasticRequest): F[HttpResponse] = {
    val f = request.entity match {
      case Some(entity) =>
        async(request.method, request.endpoint, request.params, request.headers, entity).send(sttpBackend)
      case None         => async(request.method, request.endpoint, request.params, request.headers).send(sttpBackend)
    }

    f.map(processResponse)
  }
}

object SttpRequestHttpClient {

  private implicit def futureMonad(implicit ec: ExecutionContext): MonadError[Future] = new FutureMonad()

  private implicit def defaultSttpBackend: SttpBackend[Future, Any] = HttpClientFutureBackend()

  /** Instantiate an [[SttpRequestHttpClient]] with reasonable defaults for the implicit parameters. */
  def apply(nodeEndpoint: ElasticNodeEndpoint)(implicit ec: ExecutionContext): SttpRequestHttpClient[Future] =
    new SttpRequestHttpClient(nodeEndpoint)

  /** Alternative constructor for backwards compatibility. */
  @deprecated("Use the constructor which takes an ElasticNodeEndpoint", "7.3.2")
  def apply(clientUri: ElasticsearchClientUri)(implicit ec: ExecutionContext): SttpRequestHttpClient[Future] = {
    val endpoint = ElasticNodeEndpoint("http", clientUri.hosts.head._1, clientUri.hosts.head._2, None)

    new SttpRequestHttpClient[Future](endpoint)
  }

}
