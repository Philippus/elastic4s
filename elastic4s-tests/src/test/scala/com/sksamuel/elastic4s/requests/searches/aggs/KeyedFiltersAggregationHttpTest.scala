package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class KeyedFiltersAggregationHttpTest extends FreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("keyedfiltersagg")
    }.await
  }

  client.execute {
    createIndex("keyedfiltersagg") mappings {
      mapping("buildings") fields(
        textField("name").fielddata(true),
        intField("height").stored(true)
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("keyedfiltersagg/buildings") fields("name" -> "Willis Tower", "height" -> 1244),
      indexInto("keyedfiltersagg/buildings") fields("name" -> "Burj Kalifa", "height" -> 2456),
      indexInto("keyedfiltersagg/buildings") fields("name" -> "Tower of London", "height" -> 169),
      indexInto("keyedfiltersagg/buildings") fields("name" -> "London Bridge", "height" -> 63)
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "filters agg" - {
    "should create buckets matching the query" in {

      val resp = client.execute {
        search("keyedfiltersagg").matchAllQuery().aggs {
          filtersAggregation("agg1").queries(Seq("first" -> matchQuery("name", "london"), "second" -> matchQuery("name", "tower"))).subaggs {
            sumAgg("agg2", "height")
          }
        }
      }.await.result
      resp.totalHits shouldBe 4
      resp.aggs.keyedFilters("agg1").aggResults("first").docCount shouldBe 2
      resp.aggs.keyedFilters("agg1").aggResults("first").sum("agg2").value shouldBe 232
    }

    "should create other buckets with the default key" in {

      val resp = client.execute {
        search("keyedfiltersagg").matchAllQuery().aggs {
          filtersAggregation("agg1")
            .queries(Seq("first" -> matchQuery("name", "london"), "second" -> matchQuery("name", "tower")))
            .otherBucket(true)
            .subaggs {
              sumAgg("agg2", "height")
            }
        }
      }.await.result
      resp.totalHits shouldBe 4
      resp.aggs.keyedFilters("agg1").aggResults("_other_").docCount shouldBe 1
      resp.aggs.keyedFilters("agg1").aggResults("_other_").sum("agg2").value shouldBe 2456
    }

    "should create other buckets with a specified key" in {

      val resp = client.execute {
        search("keyedfiltersagg").matchAllQuery().aggs {
          filtersAggregation("agg1")
            .queries(Seq("first" -> matchQuery("name", "london"), "second" -> matchQuery("name", "tower")))
            .otherBucketKey("otherBuildings")
            .subaggs {
              sumAgg("agg2", "height")
            }
        }
      }.await.result
      resp.totalHits shouldBe 4
      resp.aggs.keyedFilters("agg1").aggResults("otherBuildings").docCount shouldBe 1
      resp.aggs.keyedFilters("agg1").aggResults("otherBuildings").sum("agg2").value shouldBe 2456
    }
  }
}
