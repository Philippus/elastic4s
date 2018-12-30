package com.sksamuel.elastic4s.http

import java.io.InputStream
import java.nio.charset.Charset
import java.util.zip.GZIPInputStream

import com.sksamuel.elastic4s.{ElasticClient, ElasticNodeEndpoint, ElasticProperties, ElasticRequest, ElasticsearchClientUri, Executor, Functor, HttpClient, HttpEntity, HttpResponse, Show}
import com.sksamuel.exts.Logging
import org.apache.http.HttpHost
import org.apache.http.client.config.RequestConfig
import org.apache.http.entity.{AbstractHttpEntity, ContentType, FileEntity, InputStreamEntity, StringEntity}
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.elasticsearch.client.RestClientBuilder.{HttpClientConfigCallback, RequestConfigCallback}
import org.elasticsearch.client.{Request, ResponseException, ResponseListener, RestClient}

import scala.io.{Codec, Source}
import scala.language.higherKinds

case class JavaClientExceptionWrapper(t: Throwable) extends RuntimeException(t)

// an implementation of the elastic4s HttpRequestClient that wraps the elasticsearch java client
class JavaClient(client: RestClient) extends HttpClient {

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
    logger.debug("Http Response {}", r)
    HttpResponse(r.getStatusLine.getStatusCode, entity, headers)
  }

  override def send(req: ElasticRequest, callback: Either[Throwable, HttpResponse] => Unit): Unit = {
    if (logger.isDebugEnabled) {
      logger.debug("Executing elastic request {}", Show[ElasticRequest].show(req))
    }

    val l = new ResponseListener {
      override def onSuccess(r: org.elasticsearch.client.Response): Unit = callback(Right(fromResponse(r)))
      override def onFailure(e: Exception): Unit = e match {
        case re: ResponseException => callback(Right(fromResponse(re.getResponse)))
        case t => callback(Left(JavaClientExceptionWrapper(t)))
      }
    }

    val request = new Request(req.method, req.endpoint)
    req.params.foreach { case (key, value) => request.addParameter(key, value) }
    req.entity.map(apacheEntity).foreach(request.setEntity)
    client.performRequestAsync(request, l)
  }

  override def close(): Unit = client.close()

  private def isEntityGziped(entity: org.apache.http.HttpEntity): Boolean = {
    Option(entity.getContentEncoding).flatMap(x => Option(x.getValue)).contains("gzip")
  }
}

object JavaClient extends Logging {

  /**
    * Creates a new [[ElasticClient]] from an existing Elasticsearch Java API [[RestClient]].
    *
    * @param client the Java client to wrap
    * @return newly created Scala client
    */
  def fromRestClient[F[_] : Functor : Executor](client: RestClient): ElasticClient = ElasticClient(new JavaClient(client))

  /**
    * Creates a new [[ElasticClient]] using the elasticsearch Java API rest client
    * as the underlying client. Optional callbacks can be passed in to configure the client.
    */
  def apply[F[_] : Functor : Executor](props: ElasticProperties): ElasticClient =
    apply(props, NoOpRequestConfigCallback, NoOpHttpClientConfigCallback)

  /**
    * Creates a new [[ElasticClient]] using the elasticsearch Java API rest client
    * as the underlying client. Optional callbacks can be passed in to configure the client.
    */
  def apply[F[_] : Functor : Executor](props: ElasticProperties,
                                       requestConfigCallback: RequestConfigCallback,
                                       httpClientConfigCallback: HttpClientConfigCallback): ElasticClient = {
    val hosts = props.endpoints.map {
      case ElasticNodeEndpoint(protocol, host, port, _) => new HttpHost(host, port, protocol)
    }
    logger.info(s"Creating HTTP client on ${hosts.mkString(",")}")

    val client = RestClient
      .builder(hosts: _*)
      .setRequestConfigCallback(requestConfigCallback)
      .setHttpClientConfigCallback(httpClientConfigCallback)
      .build()

    fromRestClient(client)
  }

  /**
    * Creates a new [[ElasticClient]] using the elasticsearch Java API rest client
    * as the underlying client. Optional callbacks can be passed in to configure the client.
    *
    * Alternatively, create a [[RestClient]] manually and invoke [[fromRestClient(RestClient)]].
    */
  @deprecated("Use apply(ElasticProperties)", "6.3.3")
  def apply[F[_] : Functor : Executor](uri: ElasticsearchClientUri,
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

    fromRestClient(client)
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
