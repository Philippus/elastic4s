package com.sksamuel.elastic4s.filters

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.scalatest.{Matchers, WordSpec}

class ExistsQueryDefinitionTest extends WordSpec with ElasticSugar with Matchers {

  client.execute(
    bulk(
      index into "person" / "interest" fields (
        "name" -> "reese",
        "weapon" -> "revolver"
      ),
      index into "person" / "interest" fields (
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
        search in "person" / "interest" postFilter {
          existsQuery("name")
        }
      }.await.totalHits shouldBe 2
    }
    "not match null fields" in {
      client.execute {
        search in "person" / "interest" postFilter {
          existsQuery("place")
        }
      }.await.totalHits shouldBe 0
    }
  }
}
