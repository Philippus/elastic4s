package com.sksamuel.elastic4s.aws

import com.sksamuel.elastic4s.{ElasticClient, ElasticsearchClientUri}
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, AwsCredentialsProviderChain, StaticCredentialsProvider}
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain
import com.sksamuel.elastic4s.http.JavaClient
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.apache.http.protocol.HttpContext
import org.apache.http.{HttpRequest, HttpRequestInterceptor}
import software.amazon.awssdk.regions.Region

case class Aws4ElasticConfig(endpoint: String, key: String, secret: String, region: String, service: String = "es") {
  require(key.length > 16 && key.length < 128 && key.matches("[\\w]+"), "Key id must be between 16 and 128 characters.")
}

object Aws4ElasticClient {

  /**
    * Creates ES HttpClient with aws4 request signer interceptor using custom config (key, secret, region and service).
    */
  def apply(config: Aws4ElasticConfig): ElasticClient = {
    val elasticUri = ElasticsearchClientUri(config.endpoint)
    JavaClient(elasticUri, httpClientConfigCallback = new SignedClientConfig(config))
  }

  /**
    * Convenience method to create ES HttpClient with aws4 request signer interceptor using default aws environment variables.
    * See <a href="http://docs.aws.amazon.com/cli/latest/userguide/cli-environment.html">amazon environment variables documentation</a>
    */
  def apply(endpoint: String): ElasticClient = {
    val elasticUri = ElasticsearchClientUri(endpoint)
    JavaClient(elasticUri, httpClientConfigCallback = new DefaultSignedClientConfig)
  }

}

private class SignedClientConfig(config: Aws4ElasticConfig) extends HttpClientConfigCallback {
  override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder =
    httpClientBuilder.addInterceptorLast(new Aws4HttpRequestInterceptor(config))
}

private class DefaultSignedClientConfig extends HttpClientConfigCallback {
  override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder =
    httpClientBuilder.addInterceptorLast(new DefaultAws4HttpRequestInterceptor)
}

private class Aws4HttpRequestInterceptor(config: Aws4ElasticConfig) extends HttpRequestInterceptor {
  private val chainProvider = StaticCredentialsProvider.create(AwsBasicCredentials.create(config.key, config.secret))
  private val signer        = new Aws4RequestSigner(chainProvider, Region.of(config.region), config.service)

  override def process(request: HttpRequest, context: HttpContext): Unit = signer.withAws4Headers(request)

}

/**
  * Default Request Interceptor for convenience. Uses the default environment variable names.
  * See <a href="http://docs.aws.amazon.com/cli/latest/userguide/cli-environment.html">amazon environment variable documentation</a>
  * See <a href="https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/java-dg-region-selection.html"> default region selection</a>
  */
private class DefaultAws4HttpRequestInterceptor extends HttpRequestInterceptor {
  private val defaultChainProvider = AwsCredentialsProviderChain.builder.build
  private val regionProvider       = new DefaultAwsRegionProviderChain
  private val signer               = new Aws4RequestSigner(defaultChainProvider, regionProvider.getRegion)

  override def process(request: HttpRequest, context: HttpContext): Unit = signer.withAws4Headers(request)

}
