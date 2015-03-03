package com.sksamuel.elastic4s.filter

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.ElasticSugar
import org.scalatest.{ WordSpec, Matchers }

class ExistsFilterDefinition extends WordSpec with ElasticSugar with Matchers {

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

  "exists filter" should {
    "match non-null fields" in {
      client.execute {
        search in "person" / "interest" postFilter {
          existsFilter("name")
        }
      }.await.getHits.getTotalHits shouldBe 2
    }
    "not match null fields" in {
      client.execute {
        search in "person" / "interest" postFilter {
          existsFilter("place")
        }
      }.await.getHits.getTotalHits shouldBe 0
    }
  }
}
