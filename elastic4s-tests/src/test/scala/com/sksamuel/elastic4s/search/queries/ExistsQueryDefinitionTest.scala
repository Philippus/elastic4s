package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class ExistsQueryDefinitionTest extends WordSpec with DiscoveryLocalNodeProvider with Matchers with ElasticDsl {

  Try {
    http.execute {
      deleteIndex("person")
    }.await
  }

  http.execute(
    bulk(
      indexInto("person" / "interest") fields(
        "name" -> "reese",
        "weapon" -> "revolver"
      ),
      indexInto("person" / "interest") fields(
        "name" -> "finch",
        "weapon" -> "computer"
      )
    ).immediateRefresh()
  ).await

  "exists query" should {
    "match non-null fields" in {
      http.execute {
        search("person" / "interest") postFilter {
          existsQuery("name")
        }
      }.await.totalHits shouldBe 2
    }
    "not match null fields" in {
      http.execute {
        search("person" / "interest") postFilter {
          existsQuery("place")
        }
      }.await.totalHits shouldBe 0
    }
  }
}
