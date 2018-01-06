package com.sksamuel.elastic4s.sttp

import java.io._
import java.nio.file.Files

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.HttpEntity.{FileEntity, InputStreamEntity, StringEntity}
import com.sksamuel.elastic4s.http.{HttpEntity, HttpRequestClient, HttpResponse}
import com.sksamuel.exts.OptionImplicits._
import com.softwaremill.sttp._
import com.softwaremill.sttp.asynchttpclient.future.AsyncHttpClientFutureBackend

import scala.concurrent.Future
import scala.io.Source

class SttpRequestHttpClient(clientUri: ElasticsearchClientUri) extends HttpRequestClient {

  implicit val sttpBackend: SttpBackend[Future, Nothing] = AsyncHttpClientFutureBackend()

  private def request(method: String, endpoint: String, params: Map[String, Any]): Request[String, Nothing] = {
    val url = uri"http://${clientUri.hosts.head._1}:${clientUri.hosts.head._2}/$endpoint?$params"
    method.toLowerCase match {
      case "GET" => sttp.get(url)
      case "HEAD" => sttp.head(url)
      case "POST" => sttp.get(url)
      case "PUT" => sttp.put(url)
      case "DELETE" => sttp.delete(url)
    }
  }

  private def processResponse(f: Future[Response[String]]): Future[HttpResponse] = {
    f.map { resp =>
      val entity = resp.body match {
        case Left(e) => None
        case Right(body) => HttpEntity.StringEntity(body, resp.contentType).some
      }
      HttpResponse(resp.code, entity, resp.headers.toMap)
    }
  }

  override def async(method: String, endpoint: String, params: Map[String, Any]): Future[HttpResponse] = {
    processResponse(request(method, endpoint, params).send)
  }

  override def async(method: String, endpoint: String, params: Map[String, Any], entity: HttpEntity): Future[HttpResponse] = {
    val r = request(method, endpoint, params)
    val r2 = entity.contentType.fold(r)(r.contentType)
    val r3 = entity match {
      case StringEntity(content: String, _) => r2.body(content)
      case InputStreamEntity(in: InputStream, _) => r2.body(Source.fromInputStream(in, "UTF8").getLines().mkString("\n"))
      case FileEntity(file: File, _) => r2.body(Files.readAllBytes(file.toPath))
    }
    processResponse(r3.send())
  }

  override def close(): Unit = sttpBackend.close()
}
