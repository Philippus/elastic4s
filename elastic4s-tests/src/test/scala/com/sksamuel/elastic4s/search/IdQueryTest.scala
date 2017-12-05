package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.{DiscoveryLocalNodeProvider, ElasticSugar, HttpElasticSugar}
import org.scalatest.{FlatSpec, Matchers}

class IdQueryTest extends FlatSpec with HttpElasticSugar with Matchers with ElasticDsl with DiscoveryLocalNodeProvider {

  http.execute {
    createIndex("sodas")
  }.await

  http.execute {
    bulk(
      indexInto("sodas/zero").fields("name" -> "sprite zero", "style" -> "lemonade") id "5",
      indexInto("sodas/zero").fields("name" -> "coke zero", "style" -> "cola") id "9"
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "id query" should "find by id" in {
    val resp = http.execute {
      search("sodas/zero").query {
        idsQuery(5)
      }
    }.await.right.get.result

    resp.totalHits shouldBe 1
    resp.hits.hits.head.sourceField("name") shouldBe "sprite zero"
  }

  it should "find multiple ids" in {
    val resp = http.execute {
      search("sodas/zero").query {
        idsQuery(5, 9)
      }
    }.await.right.get.result

    resp.totalHits shouldBe 2
    resp.hits.hits.map(_.sourceField("name")).toSet shouldBe Set("sprite zero", "coke zero")
  }
}
