package com.sksamuel.elastic4s.testkit

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.ElasticClient

trait DockerTests extends com.sksamuel.elastic4s.http.ElasticDsl with ClientProvider {
  val client = ElasticClient(ElasticsearchClientUri("http://localhost:9200"))
}
