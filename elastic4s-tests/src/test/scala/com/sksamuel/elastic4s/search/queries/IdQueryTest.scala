package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FlatSpec, Matchers}

class IdQueryTest extends FlatSpec with Matchers with DockerTests {

  client.execute {
    createIndex("sodas")
  }.await

  client.execute {
    bulk(
      indexInto("sodas/zero").fields("name" -> "sprite zero", "style" -> "lemonade") id "5",
      indexInto("sodas/zero").fields("name" -> "coke zero", "style" -> "cola") id "9"
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "id query" should "find by id" in {
    val resp = client.execute {
      search("sodas/zero").query {
        idsQuery(5)
      }
    }.await.result

    resp.totalHits shouldBe 1
    resp.hits.hits.head.sourceField("name") shouldBe "sprite zero"
  }

  it should "find multiple ids" in {
    val resp = client.execute {
      search("sodas/zero").query {
        idsQuery(5, 9)
      }
    }.await.result

    resp.totalHits shouldBe 2
    resp.hits.hits.map(_.sourceField("name")).toSet shouldBe Set("sprite zero", "coke zero")
  }
}
