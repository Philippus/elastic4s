package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.scalatest.{Matchers, WordSpec}

class ExistsQueryDefinitionTest extends WordSpec with SharedElasticSugar with Matchers {

  client.execute(
    bulk(
      indexInto("person" / "interest") fields(
        "name" -> "reese",
        "weapon" -> "revolver"
      ),
      indexInto("person" / "interest") fields(
        "name" -> "finch",
        "weapon" -> "computer"
      )
    )
  ).await

  refresh("person")
  blockUntilCount(2, "person")

  "exists query" should {
    "match non-null fields" in {
      client.execute {
        search("person" / "interest") postFilter {
          existsQuery("name")
        }
      }.await.totalHits shouldBe 2
    }
    "not match null fields" in {
      client.execute {
        search("person" / "interest") postFilter {
          existsQuery("place")
        }
      }.await.totalHits shouldBe 0
    }
  }
}
