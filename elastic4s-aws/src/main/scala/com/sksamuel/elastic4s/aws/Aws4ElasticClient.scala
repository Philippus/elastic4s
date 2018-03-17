package com.sksamuel.elastic4s.aws

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.ElasticClient
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials, DefaultAWSCredentialsProviderChain}
import com.amazonaws.regions.DefaultAwsRegionProviderChain
import com.amazonaws.auth._
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.apache.http.protocol.HttpContext
import org.apache.http.{HttpRequest, HttpRequestInterceptor}

case class Aws4ElasticConfig(endpoint: String, key: String, secret: String, region: String, service: String = "es") {
  require(key.length > 16 && key.length < 128 && key.matches("[\\w]+"), "Key id must be between 16 and 128 characters.")
}

/**
  * Variant of Aws4ElasticConfig which allows an AWSCredentialsProvider to be passed in directly
  */
case class Aws4ElasticConfigWithProvider(
  endpoint: String,
  credentialsProvider: AWSCredentialsProvider,
  region: String,
  service: String
)

object Aws4ElasticConfigWithProvider {
  /**
    * Convenience method to convert an Aws4ElasticConfig to an Aws4ElasticConfigWithProvider
    */
  def fromStandardConfig(config: Aws4ElasticConfig): Aws4ElasticConfigWithProvider = {
    val credentialsProvider = new AWSStaticCredentialsProvider(new BasicAWSCredentials(config.key, config.secret))
    Aws4ElasticConfigWithProvider(config.endpoint, credentialsProvider, config.region, config.service)
  }
}

object Aws4ElasticClient {

  /**
    * Creates ES HttpClient with aws4 request signer interceptor using custom config (credential provider, region and service).
    */
  def apply(config: Aws4ElasticConfigWithProvider): ElasticClient = {
    val elasticUri = ElasticsearchClientUri(config.endpoint)
    ElasticClient(elasticUri, httpClientConfigCallback = new SignedClientConfig(config))
  }

  /**
    * Creates ES HttpClient with aws4 request signer interceptor using custom config (key, secret, region and service)
    */
  def apply(config: Aws4ElasticConfig): ElasticClient =
    Aws4ElasticClient(Aws4ElasticConfigWithProvider.fromStandardConfig(config))

  /**
    * Convenience method to create ES HttpClient with aws4 request signer interceptor using default aws environment variables.
    * See <a href="http://docs.aws.amazon.com/cli/latest/userguide/cli-environment.html">amazon environment variables documentation</a>
    */
  def apply(endpoint: String): ElasticClient = {
    val elasticUri = ElasticsearchClientUri(endpoint)
    ElasticClient(elasticUri, httpClientConfigCallback = new DefaultSignedClientConfig)
  }

}

private class SignedClientConfig(config: Aws4ElasticConfigWithProvider) extends HttpClientConfigCallback {
  override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder =
    httpClientBuilder.addInterceptorLast(new Aws4HttpRequestInterceptor(config))
}

private class DefaultSignedClientConfig extends HttpClientConfigCallback {
  override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder =
    httpClientBuilder.addInterceptorLast(new DefaultAws4HttpRequestInterceptor)
}

private class Aws4HttpRequestInterceptor(config: Aws4ElasticConfigWithProvider) extends HttpRequestInterceptor {
  private val signer        = new Aws4RequestSigner(config.credentialsProvider, config.region, config.service)

  override def process(request: HttpRequest, context: HttpContext): Unit = signer.withAws4Headers(request)

}

/**
  * Default Request Interceptor for convenience. Uses the default environment variable names.
  * See <a href="http://docs.aws.amazon.com/cli/latest/userguide/cli-environment.html">amazon environment variable documentation</a>
  * See <a href="https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/java-dg-region-selection.html"> default region selection</a>
  */
private class DefaultAws4HttpRequestInterceptor extends HttpRequestInterceptor {
  private val defaultChainProvider = new DefaultAWSCredentialsProviderChain
  private val regionProvider       = new DefaultAwsRegionProviderChain
  private val signer               = new Aws4RequestSigner(defaultChainProvider, regionProvider.getRegion)

  override def process(request: HttpRequest, context: HttpContext): Unit = signer.withAws4Headers(request)

}
