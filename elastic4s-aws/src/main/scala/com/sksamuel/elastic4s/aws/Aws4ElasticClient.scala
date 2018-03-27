package com.sksamuel.elastic4s.aws

import com.amazonaws.auth.{AWSCredentialsProvider, AWSStaticCredentialsProvider, BasicAWSCredentials, DefaultAWSCredentialsProviderChain}
import com.amazonaws.regions.DefaultAwsRegionProviderChain
import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.HttpClient
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.apache.http.protocol.HttpContext
import org.apache.http.{HttpRequest, HttpRequestInterceptor}
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback

case class Aws4ElasticConfig(endpoint: String, key: String, secret: String, region: String, service: String = "es") {
  require(key.length > 16 && key.length < 128 && key.matches("[\\w]+"), "Key id must be between 16 and 128 characters.")
}

object Aws4ElasticClient {

  /**
    * Creates ES HttpClient with aws4 request signer interceptor using custom config (key, secret, region and service).
    */
  def apply(config: Aws4ElasticConfig): HttpClient = {
    val elasticUri = ElasticsearchClientUri(config.endpoint)
    HttpClient(elasticUri, httpClientConfigCallback = new SignedClient(new AWSStaticCredentialsProvider(new BasicAWSCredentials(config.key, config.secret)), config.region))
  }

  /**
    * Convenience method to create ES HttpClient with aws4 request signer interceptor using default aws environment variables.
    * See <a href="http://docs.aws.amazon.com/cli/latest/userguide/cli-environment.html">amazon environment variables documentation</a>
    */
  def apply(endpoint: String): HttpClient = {
    val elasticUri = ElasticsearchClientUri(endpoint)
    HttpClient(elasticUri, httpClientConfigCallback = new SignedClient(new DefaultAWSCredentialsProviderChain, (new DefaultAwsRegionProviderChain).getRegion))
  }

  /**
    * Creates ES HttpClient with custom AWS credential and region providers.
    */
  def apply(endpoint: String, credentialProvider: AWSCredentialsProvider, region: String): HttpClient = {
    val elasticUri = ElasticsearchClientUri(endpoint)
    HttpClient(elasticUri, httpClientConfigCallback = new SignedClient(credentialProvider, region))
  }

}

private class SignedClient(chainProvider: AWSCredentialsProvider, region: String) extends HttpClientConfigCallback {
  override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder =
    httpClientBuilder.addInterceptorLast(new Aws4HttpRequestInterceptor(chainProvider, region))
}

private class Aws4HttpRequestInterceptor(credentialProvider: AWSCredentialsProvider, region: String) extends HttpRequestInterceptor {
  private val signer = new Aws4RequestSigner(credentialProvider, region)

  override def process(request: HttpRequest, context: HttpContext): Unit = signer.withAws4Headers(request)

}
