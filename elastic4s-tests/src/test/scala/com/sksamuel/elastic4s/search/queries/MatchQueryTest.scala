package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.testkit.{DiscoveryLocalNodeProvider, ElasticSugar}
import org.scalatest.{Matchers, WordSpec}

class MatchQueryTest extends WordSpec with Matchers with ElasticSugar with DiscoveryLocalNodeProvider with ElasticDsl {

  client.execute {
    bulk(
      indexInto("elite/ships") fields("name" -> "vulture", "manufacturer" -> "Core Dynamics"),
      indexInto("elite/ships") fields("name" -> "sidewinder", "manufacturer" -> "Core Dynamics"),
      indexInto("elite/ships") fields("name" -> "cobra mark 3", "manufacturer" -> "Core Dynamics")
    ).immediateRefresh()
  }.await

  "a match query" should {
    "use same analyzer as inputs" in {
      val resp = client.execute {
        search("elite" / "ships") query {
          matchQuery("name", "VULTURE")
        }
      }.await
      resp.totalHits shouldBe 1
    }
  }
}
