package com.sksamuel.elastic4s.cat

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{FlatSpec, Matchers}

class CatNodesTest extends FlatSpec with Matchers with SharedElasticSugar with ElasticDsl {

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    bulk(
      indexInto("catnodes1/landmarks").fields("name" -> "hampton court palace"),
      indexInto("catnodes2/landmarks").fields("name" -> "hampton court palace")
    ).refresh(RefreshPolicy.IMMEDIATE)
  }.await

  "cats nodes" should "return all nodes" in {
    val result = http.execute {
      catNodes()
    }.await.head
    result.load_1m > 0 shouldBe true
    result.load_5m > 0 shouldBe true
    result.load_15m > 0 shouldBe true
    result.ramPercent > 0 shouldBe true
  }
}
