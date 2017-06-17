package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Try

class MultiSearchHttpTest
  extends FlatSpec
    with DiscoveryLocalNodeProvider
    with Matchers
    with ElasticDsl {

  Try {
    http.execute {
      deleteIndex("jtull")
    }.await
  }

  http.execute {
    createIndex("jtull")
  }.await

  http.execute {
    bulk(
      indexInto("jtull" / "albums") fields ("name" -> "aqualung") id 14,
      indexInto("jtull" / "albums") fields ("name" -> "passion play") id 51
    ).refresh(RefreshPolicy.Immediate)
  }.await

  "a multi search request" should "perform search for all queries" in {

    val resp = http.execute {
      multi(
        search("jtull") query matchQuery("name", "aqualung"),
        search("jtull") query "passion",
        search("jtull" / "albums") query matchAllQuery()
      )
    }.await

    resp.successes.size shouldBe 3
    resp.size shouldBe 3

    resp.successes.head.hits.hits.head.id shouldBe "14"
    resp.successes.tail.head.hits.hits.head.id shouldBe "51"

    resp.successes.head.totalHits shouldBe 1
    resp.successes.tail.head.totalHits shouldBe 1
    resp.successes.last.totalHits shouldBe 2
  }

  it should "correctly set errored and successful items" in {
    val resp = http.execute {
      multi(
        search("jtull") query matchQuery("name", "aqualung"),
        search("unknown") query matchAllQuery()
      )
    }.await

    resp.successes.size shouldBe 1
    resp.failures.size shouldBe 1
    resp.items.head.index shouldBe 0
    resp.items.head.status shouldBe 200
    resp.items.last.index shouldBe 1
    resp.items.last.status shouldBe 404
  }
}
