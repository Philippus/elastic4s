package com.sksamuel.elastic4s

import org.scalatest.{FunSuite, Matchers}

class Issue900 extends FunSuite with Matchers {

  test("elastic client match error") {
    ElasticClient.transport(ElasticsearchClientUri("localhost", 9300))
  }
}
