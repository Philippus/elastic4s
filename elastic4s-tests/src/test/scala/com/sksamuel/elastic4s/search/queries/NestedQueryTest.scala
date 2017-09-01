package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.{ElasticDsl, RefreshPolicy}
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{Matchers, WordSpec}

import scala.util.Try

class NestedQueryTest extends WordSpec with DiscoveryLocalNodeProvider with Matchers with ElasticDsl {

  Try {
    client.execute {
      deleteIndex("nested")
    }.await
  }

  client.execute {
    createIndex("nested").mappings(
      mapping("places").fields(
        keywordField("name"),
        nestedField("states")
      )
    )
  }

  client.execute(
    bulk(
      indexInto("nested" / "places") fields(
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
      indexInto("nested" / "places") fields(
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
        search("nested" / "places") query {
          nestedQuery("states").query {
            boolQuery.must(
              matchQuery("states.name", "Montana"),
              matchQuery("states.entry", 1889)
            )
          }
        }
      }.await.totalHits shouldBe 1
    }
  }
}
