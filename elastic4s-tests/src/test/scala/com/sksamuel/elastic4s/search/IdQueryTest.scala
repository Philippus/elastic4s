package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{FlatSpec, Matchers}

class IdQueryTest extends FlatSpec with ElasticSugar with Matchers with ElasticDsl {

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    createIndex("sodas")
  }.await

  http.execute {
    bulk(
      indexInto("sodas/zero").fields("name" -> "sprite zero", "style" -> "lemonade") id 5,
      indexInto("sodas/zero").fields("name" -> "coke zero", "style" -> "cola") id 9
    ).refresh(RefreshPolicy.IMMEDIATE)
  }.await

  "id query" should "find by id" in {
    val resp = http.execute {
      search("sodas/zero").query {
        idsQuery(5)
      }
    }.await

    resp.totalHits shouldBe 1
    resp.hits.hits.head.sourceField("name") shouldBe "sprite zero"
  }

  it should "find multiple ids" in {
    val resp = http.execute {
      search("sodas/zero").query {
        idsQuery(5, 9)
      }
    }.await

    resp.totalHits shouldBe 2
    resp.hits.hits.map(_.sourceField("name")).toSet shouldBe Set("sprite zero", "coke zero")
  }
}
