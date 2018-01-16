package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.http.HttpClient

trait DockerTests extends com.sksamuel.elastic4s.http.ElasticDsl {
  val client = HttpClient(ElasticsearchClientUri("http://localhost:9200"))
}
