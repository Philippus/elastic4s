package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.Try

class NestedQueryTest extends AnyWordSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("nested")
    }.await
  }

  client.execute {
    createIndex("nested").mapping(
      mapping(
        keywordField("name"),
        nestedField("states")
      )
    )
  }.await

  client.execute(
    bulk(
      indexInto("nested") fields(
        "name" -> "usa",
        "states" -> Seq(
          Map(
            "name" -> "Montana",
            "capital" -> "Helena",
            "entry" -> 1889
          ), Map(
            "name" -> "South Dakota",
            "capital" -> "Pierre",
            "entry" -> 1889
          )
        )
      ),
      indexInto("nested") fields(
        "name" -> "fictional usa",
        "states" -> Seq(
          Map(
            "name" -> "Old Jersey",
            "capital" -> "Trenton",
            "entry" -> 1889
          ), Map(
            "name" -> "Montana",
            "capital" -> "Helena",
            "entry" -> 1567
          )
        )
      )
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "nested query" should {
    "match against nested objects" in {
      client.execute {
        search("nested") query {
          nestedQuery("states").query {
            boolQuery().must(
              matchQuery("states.name", "Montana"),
              matchQuery("states.entry", 1889)
            )
          }
        }
      }.await.result.totalHits shouldBe 1
    }
  }
}
