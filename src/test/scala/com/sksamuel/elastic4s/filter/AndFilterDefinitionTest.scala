package com.sksamuel.elastic4s.filter

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.ElasticSugar
import org.scalatest.{FlatSpec, Matchers, WordSpec}

class AndFilterDefinitionTest extends FlatSpec with ElasticSugar with Matchers {

  client.execute(
    bulk(
      index into "walking" / "dead" fields(
        "name" -> "darryl",
        "weapon" -> "crossbow"
        ),
      index into "walking" / "dead" fields(
        "name" -> "rick",
        "weapon" -> "pistol"
        ),
      index into "walking" / "dead" fields(
        "name" -> "michon",
        "weapon" -> "sword"
        )
    )
  ).await

  refresh("walking")
  blockUntilCount(3, "walking")

  "and filter" should "include matches on all queries" in {
    client.execute {
      search in "walking" / "dead" postFilter {
        and(
          termFilter("name", "darryl"),
          termFilter("weapon", "crossbow")
        )
      }
    }.await.getHits.getTotalHits shouldBe 1
  }

  "and filter" should "exclude not matches on all queries" in {
    client.execute {
      search in "walking" / "dead" postFilter {
        and(
          termFilter("name", "darryl"),
          termFilter("weapon", "sword")
        )
      }
    }.await.getHits.getTotalHits shouldBe 0
  }
}
