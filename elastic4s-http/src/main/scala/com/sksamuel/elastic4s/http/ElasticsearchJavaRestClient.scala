package com.sksamuel.elastic4s.http

import java.nio.charset.Charset

import org.apache.http.client.config.RequestConfig
import org.apache.http.entity.{ContentType, FileEntity, InputStreamEntity, StringEntity}
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.elasticsearch.client.RestClientBuilder.{HttpClientConfigCallback, RequestConfigCallback}
import org.elasticsearch.client.{ResponseException, ResponseListener, RestClient}

import scala.concurrent.{Future, Promise}
import scala.io.{Codec, Source}

case class JavaClientExceptionWrapper(t: Throwable) extends RuntimeException(t)

// an implementation of the elastic4s HttpRequestClient that wraps the elasticsearch java client
class ElasticsearchJavaRestClient(client: RestClient) extends HttpRequestClient {

  import scala.collection.JavaConverters._

  private def future(callback: ResponseListener => Any): Future[HttpResponse] = {
    val p = Promise[HttpResponse]()
    callback(new ResponseListener {

      def fromResponse(r: org.elasticsearch.client.Response): HttpResponse = {
        val entity = Option(r.getEntity).map { entity =>
          val contentEncoding = Option(entity.getContentEncoding).map(_.getValue).getOrElse("UTF-8")
          implicit val codec = Codec(Charset.forName(contentEncoding))
          val body = Source.fromInputStream(entity.getContent).mkString
          HttpEntity.StringEntity(body, Some(contentEncoding))
        }
        val headers = r.getHeaders.map { header => header.getName -> header.getValue }.toMap
        logger.debug(s"Http Response $r")
        HttpResponse(r.getStatusLine.getStatusCode, entity, headers)
      }

      override def onSuccess(r: org.elasticsearch.client.Response): Unit = p.trySuccess(fromResponse(r))
      override def onFailure(e: Exception): Unit = e match {
        case re: ResponseException => p.trySuccess(fromResponse(re.getResponse))
        case t => p.tryFailure(JavaClientExceptionWrapper(t))
      }
    })
    p.future
  }

  override def async(method: String,
                     endpoint: String,
                     params: Map[String, Any]): Future[HttpResponse] = {
    logger.debug(s"Executing elastic request $method:$endpoint?${params.map { case (k, v) => k + "=" + v }.mkString("&")}")
    val callback = client.performRequestAsync(method, endpoint, params.mapValues(_.toString).asJava, _: ResponseListener)
    future(callback)
  }

  override def async(method: String,
                     endpoint: String,
                     params: Map[String, Any],
                     entity: HttpEntity): Future[HttpResponse] = {
    logger.debug(s"Executing elastic request $method:$endpoint?${params.map { case (k, v) => k + "=" + v }.mkString("&")}")

    val apacheEntity = entity match {
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

    val callback = client.performRequestAsync(
      method,
      endpoint,
      params.mapValues(_.toString).asJava,
      apacheEntity,
      _: ResponseListener)
    future(callback)
  }

  override def close(): Unit = client.close()
}

/**
  * RequestConfigCallback that performs a no-op on the given RequestConfig.Builder.
  *
  * Used as a default parameter to the HttpClient when no custom request
  * configuration is needed.
  *
  */
object NoOpRequestConfigCallback extends RequestConfigCallback {
  override def customizeRequestConfig(requestConfigBuilder: RequestConfig.Builder): RequestConfig.Builder = requestConfigBuilder
}

/**
  * HttpAsyncClientBuilder that performs a no-op on the given HttpAsyncClientBuilder
  *
  * Used as a default parameter to the HttpClient when no custom HttpAsync
  * configuration is needed.
  *
  */
object NoOpHttpClientConfigCallback extends HttpClientConfigCallback {
  override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder = httpClientBuilder
}
