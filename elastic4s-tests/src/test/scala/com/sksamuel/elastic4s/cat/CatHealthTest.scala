package com.sksamuel.elastic4s.cat

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{FlatSpec, Matchers}

class CatHealthTest extends FlatSpec with Matchers with SharedElasticSugar with ElasticDsl {

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    bulk(
      indexInto("cathealth/landmarks").fields("name" -> "hampton court palace")
    ).refresh(RefreshPolicy.IMMEDIATE)
  }.await


  "cat health" should "return cluster health" in {
    http.execute {
      catHealth()
    }.await.cluster shouldBe "classloader-node"
  }

}
