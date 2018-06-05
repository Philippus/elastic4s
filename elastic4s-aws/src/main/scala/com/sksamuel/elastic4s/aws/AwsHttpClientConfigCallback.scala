package com.sksamuel.elastic4s.aws

import com.amazonaws.auth.AWSCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.apache.http.protocol.HttpContext
import org.apache.http.{HttpRequest, HttpRequestInterceptor}
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback

class AwsHttpClientConfigCallback(chainProvider: AWSCredentialsProvider, region: String) extends HttpClientConfigCallback {
  override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder =
    httpClientBuilder.addInterceptorLast(new Aws4HttpRequestInterceptor(chainProvider, region))
}

private class Aws4HttpRequestInterceptor(credentialProvider: AWSCredentialsProvider, region: String) extends HttpRequestInterceptor {
  private val signer = new Aws4RequestSigner(credentialProvider, region)

  override def process(request: HttpRequest, context: HttpContext): Unit = signer.withAws4Headers(request)

}
