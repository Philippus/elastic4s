package com.sksamuel.elastic4s.http

import java.io.InputStream
import java.nio.charset.Charset
import java.util.zip.GZIPInputStream

import com.sksamuel.elastic4s.Show
import org.apache.http.client.config.RequestConfig
import org.apache.http.entity.{AbstractHttpEntity, ContentType, FileEntity, InputStreamEntity, StringEntity}
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.elasticsearch.client.{ResponseException, ResponseListener, RestClient}
import org.elasticsearch.client.RestClientBuilder.{HttpClientConfigCallback, RequestConfigCallback}

import scala.collection.JavaConverters._
import scala.io.{Codec, Source}

case class JavaClientExceptionWrapper(t: Throwable) extends RuntimeException(t)

// an implementation of the elastic4s HttpRequestClient that wraps the elasticsearch java client
class ElasticsearchJavaRestClient(client: RestClient) extends HttpClient {

  def apacheEntity(entity: HttpEntity): AbstractHttpEntity = entity match {
    case e: HttpEntity.StringEntity =>
      logger.debug(e.content)
      new StringEntity(e.content, ContentType.APPLICATION_JSON)
    case e: HttpEntity.InputStreamEntity =>
      logger.debug(e.content.toString)
      new InputStreamEntity(e.content, ContentType.APPLICATION_JSON)
    case e: HttpEntity.FileEntity =>
      logger.debug(e.content.toString)
      new FileEntity(e.content, ContentType.APPLICATION_JSON)
  }

  def fromResponse(r: org.elasticsearch.client.Response): HttpResponse = {
    val entity = Option(r.getEntity).map { entity =>
      val contentCharset = Option(ContentType.get(entity)).fold(Charset.forName("UTF-8"))(_.getCharset)
      implicit val codec: Codec = Codec(contentCharset)

      val contentStream: InputStream = {
        if (isEntityGziped(entity)) new GZIPInputStream(entity.getContent)
        else entity.getContent
      }

      val body = Source.fromInputStream(contentStream).mkString
      HttpEntity.StringEntity(body, Some(contentCharset.name()))
    }
    val headers = r.getHeaders.map { header =>
      header.getName -> header.getValue
    }.toMap
    logger.debug(s"Http Response $r")
    HttpResponse(r.getStatusLine.getStatusCode, entity, headers)
  }

  override def send(req: ElasticRequest, callback: Either[Throwable, HttpResponse] => Unit): Unit = {
    logger.debug(s"Executing elastic request ${Show[ElasticRequest].show(req)}")

    val l = new ResponseListener {
      override def onSuccess(r: org.elasticsearch.client.Response): Unit = callback(Right(fromResponse(r)))
      override def onFailure(e: Exception): Unit = e match {
        case re: ResponseException => callback(Right(fromResponse(re.getResponse)))
        case t                     => callback(Left(JavaClientExceptionWrapper(t)))
      }
    }

    val jparams = req.params.asJava

    req.entity match {
      case Some(entity) => client.performRequestAsync(req.method, req.endpoint, jparams, apacheEntity(entity), l)
      case None         => client.performRequestAsync(req.method, req.endpoint, jparams, l)
    }
  }

  override def close(): Unit = client.close()

  private def isEntityGziped(entity: org.apache.http.HttpEntity): Boolean = {
    Option(entity.getContentEncoding).flatMap(x => Option(x.getValue)).contains("gzip")
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
  override def customizeRequestConfig(requestConfigBuilder: RequestConfig.Builder): RequestConfig.Builder =
    requestConfigBuilder
}

/**
  * HttpAsyncClientBuilder that performs a no-op on the given HttpAsyncClientBuilder
  *
  * Used as a default parameter to the HttpClient when no custom HttpAsync
  * configuration is needed.
  *
  */
object NoOpHttpClientConfigCallback extends HttpClientConfigCallback {
  override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder =
    httpClientBuilder
}
