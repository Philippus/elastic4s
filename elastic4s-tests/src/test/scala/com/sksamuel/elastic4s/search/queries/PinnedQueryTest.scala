package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PinnedQueryTest extends AnyFlatSpec with Matchers with DockerTests {

  client.execute {
    createIndex("sodas")
  }.await

  client.execute {
    bulk(
      indexInto("sodas").fields("name" -> "sprite zero", "style" -> "lemonade") id "5",
      indexInto("sodas").fields("name" -> "coke zero", "style" -> "cola") id "9"
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "pinned query" should "match both ids in the correct order" in {
    val resp = client.execute {
      search("sodas").query {
        pinnedQuery(ids = List("9"), organic = matchQuery("style", "lemonade"))
      }
    }.await.result

    resp.totalHits shouldBe 2
    resp.hits.hits.head.sourceField("name") shouldBe "coke zero"
    resp.hits.hits.last.sourceField("name") shouldBe "sprite zero"
  }
}
