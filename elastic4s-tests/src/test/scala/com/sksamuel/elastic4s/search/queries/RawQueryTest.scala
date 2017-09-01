package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class RawQueryTest extends WordSpec with Matchers with ElasticDsl with DiscoveryLocalNodeProvider {

  Try {
    http.execute {
      deleteIndex("rawquerytest")
    }.await
  }

  http.execute {
    bulk(
      indexInto("rawquerytest/paris").fields("landmark" -> "montmarte", "arrondissement" -> "18"),
      indexInto("rawquerytest/paris").fields("landmark" -> "le tower eiffel", "arrondissement" -> "7")
    ).immediateRefresh()
  }.await

  "raw query" should {
    "work!" ignore {
      http.execute {
        search("*").types("paris") limit 5 rawQuery {
          """{ "prefix": { "landmark": { "prefix": "montm" } } }"""
        }
      }.await.totalHits shouldBe 1
    }
  }
}
