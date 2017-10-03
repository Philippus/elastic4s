package com.sksamuel.elastic4s.http

import java.time.{LocalDateTime, ZoneId}

import com.amazonaws.auth.{AWSCredentialsProvider, DefaultAWSCredentialsProviderChain}
import com.amazonaws.util.IOUtils
import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.exts.Logging
import io.ticofab.AwsSigner
import org.apache.http.client.methods.HttpRequestWrapper
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.apache.http.message.BasicHeader
import org.apache.http.protocol.HttpContext
import org.apache.http.{HttpEntityEnclosingRequest, HttpHost, HttpRequest, HttpRequestInterceptor}
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestClientBuilder.{HttpClientConfigCallback, RequestConfigCallback}


object AWSHttpClient extends Logging {
  def fromRestClient(client: RestClient): HttpClient =
    new HttpClient {
      override def rest: RestClient = client

      override def close(): Unit = client.close()
    }

  def apply(uri: ElasticsearchClientUri,
            requestConfigCallback: RequestConfigCallback = NoOpRequestConfigCallback,
            httpClientConfigCallback: HttpClientConfigCallback = SignedClientConfig
           ): HttpClient = {
    val httpHosts = uri.hosts.map {
      case (host, port) => new HttpHost(host, port, "https")
    }

    val client = RestClient.builder(httpHosts: _*)
      .setRequestConfigCallback(requestConfigCallback)
      .setHttpClientConfigCallback(httpClientConfigCallback)
      .build()

    AWSHttpClient.fromRestClient(client)
  }
}

object SignedClientConfig extends HttpClientConfigCallback {
  override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder = {
    httpClientBuilder.addInterceptorLast(new AWSSigningRequestInterceptor)
  }
}

class AWSSigningRequestInterceptor(private val awsCredentialsProvider: AWSCredentialsProvider = new DefaultAWSCredentialsProviderChain(),
                                   private val region: String = "ap-northeast-2",
                                   private val service: String = "es",
                                   private val dateTime: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))
                                  ) extends HttpRequestInterceptor {

  val signer = AwsSigner(awsCredentialsProvider, region, service, () => dateTime)

  override def process(request: HttpRequest, context: HttpContext): Unit = {
    val rw = request.asInstanceOf[HttpRequestWrapper]
    val newHeaders = mapHeaders(rw)
    val headers = signer.getSignedHeaders(
      rw.getURI.getRawPath,
      request.getRequestLine.getMethod,
      params(rw),
      newHeaders,
      body(request)
    )
    request.setHeaders(headers.map { case (name, value) => new BasicHeader(name, value) }.toArray)
  }

  private def body(request: HttpRequest) = {
    val original = request.asInstanceOf[HttpRequestWrapper].getOriginal
    if (!classOf[HttpEntityEnclosingRequest].isAssignableFrom(original.getClass)) None
    else {
      Option(original.asInstanceOf[HttpEntityEnclosingRequest].getEntity).flatMap(e => Option(e.getContent))
        .map(IOUtils.toByteArray)
    }
  }

  private def params(rw: HttpRequestWrapper) = {
    Option(rw.getURI.getQuery).map(_.split("&").map(_.split("=")).map(p => (p(0), p(1))).toMap).getOrElse(Map.empty)
  }

  private def mapHeaders(rw: HttpRequestWrapper) = {
    Option(rw.getAllHeaders)
      .map(_.map(h => (h.getName, h.getValue)).toMap)
      .getOrElse(Map.empty)
      .map {
        case ("Host", url) => "Host" -> url.replaceFirst(":[0-9]+", "")
        case t => t
      }
  }

}
