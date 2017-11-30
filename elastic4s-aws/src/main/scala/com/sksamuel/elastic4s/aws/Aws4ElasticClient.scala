package com.sksamuel.elastic4s.aws


import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.HttpClient
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials, DefaultAWSCredentialsProviderChain}

import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.apache.http.protocol.HttpContext
import org.apache.http.{HttpRequest, HttpRequestInterceptor}

case class Aws4ElasticConfig(endpoint: String, key: String, secret: String, region: String, service: String = "es") {
  require(key.length > 16 && key.length < 128 && key.matches("[\\w]+"), "Key id must have between 16 and 128 characters.")
}

object Aws4ElasticClient {

  /**
    * Creates ES HttpClient with aws4 request signer interceptor using custom config (key, secret, region and service).
    */
  def apply(config: Aws4ElasticConfig): HttpClient = {
    val elasticUri = ElasticsearchClientUri(config.endpoint)
    HttpClient(elasticUri, httpClientConfigCallback = new SignedClientConfig(config))
  }

  /**
    * Convenience method to create ES HttpClient with aws4 request signer interceptor using default aws environment variables.
    * See <a href="http://docs.aws.amazon.com/cli/latest/userguide/cli-environment.html">amazon environment variables documentation</a>
    */
  def apply(endpoint: String): HttpClient = {
    val elasticUri = ElasticsearchClientUri(endpoint)
    HttpClient(elasticUri, httpClientConfigCallback = new DefaultSignedClientConfig)
  }

}

private class SignedClientConfig(config: Aws4ElasticConfig) extends HttpClientConfigCallback {
  override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder = {
    httpClientBuilder.addInterceptorLast(new Aws4HttpRequestInterceptor(config))
  }
}

private class DefaultSignedClientConfig extends HttpClientConfigCallback {
  override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder = {
    httpClientBuilder.addInterceptorLast(new DefaultAws4HttpRequestInterceptor)
  }
}

private class Aws4HttpRequestInterceptor(config: Aws4ElasticConfig) extends HttpRequestInterceptor {
  private val chainProvider = new AWSStaticCredentialsProvider(new BasicAWSCredentials(config.key, config.secret))
  private val signer = new Aws4RequestSigner(chainProvider, config.region, config.service)

  override def process(request: HttpRequest, context: HttpContext): Unit = signer.withAws4Headers(request)

}

/**
  * Default Request Interceptor for convenience. Uses the default environment variable names.
  * See <a href="http://docs.aws.amazon.com/cli/latest/userguide/cli-environment.html">amazon environment variable documentation</a>
  *
  */
private class DefaultAws4HttpRequestInterceptor extends HttpRequestInterceptor {
  private val defaultChainProvider = new DefaultAWSCredentialsProviderChain
  private val region = sys.env("AWS_DEFAULT_REGION")
  private val signer = new Aws4RequestSigner(defaultChainProvider, region)

  override def process(request: HttpRequest, context: HttpContext): Unit = signer.withAws4Headers(request)

}
