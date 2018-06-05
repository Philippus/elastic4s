package com.sksamuel.elastic4s.aws

import com.amazonaws.auth.{AWSCredentialsProvider, DefaultAWSCredentialsProviderChain}
import com.amazonaws.regions.DefaultAwsRegionProviderChain
import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{HttpClient, NoOpRequestConfigCallback}
import org.elasticsearch.client.RestClientBuilder.RequestConfigCallback

object Aws4ElasticClient {

  /**
    * Creates ES HttpClient with custom AWS credential and region providers.
    */
  def apply(uri: ElasticsearchClientUri, credentialProvider: AWSCredentialsProvider = new DefaultAWSCredentialsProviderChain, region: String = (new DefaultAwsRegionProviderChain).getRegion, requestConfigCallback: RequestConfigCallback = NoOpRequestConfigCallback): HttpClient = {
    HttpClient(
      uri,
      requestConfigCallback = requestConfigCallback,
      httpClientConfigCallback = new AwsHttpClientConfigCallback(credentialProvider, region)
    )
  }

}

