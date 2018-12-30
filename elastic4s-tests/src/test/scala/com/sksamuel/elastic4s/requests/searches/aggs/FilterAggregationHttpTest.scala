package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class FilterAggregationHttpTest extends FreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("filteragg")
    }.await
  }

  client.execute {
    createIndex("filteragg") mappings {
      mapping("buildings") fields(
        textField("name").fielddata(true),
        intField("height").stored(true)
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("filteragg/buildings") fields("name" -> "Willis Tower", "height" -> 1244),
      indexInto("filteragg/buildings") fields("name" -> "Burj Kalifa", "height" -> 2456),
      indexInto("filteragg/buildings") fields("name" -> "Tower of London", "height" -> 169),
      indexInto("filteragg/buildings") fields("name" -> "London Bridge", "height" -> 63)
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "filter agg" - {
    "should create a bucket matching the query" in {

      val resp = client.execute {
        search("filteragg").matchAllQuery().aggs {
          filterAgg("agg1", matchQuery("name", "london")).subaggs {
            sumAgg("agg2", "height")
          }
        }
      }.await.result
      resp.totalHits shouldBe 4
      resp.aggs.filter("agg1").docCount shouldBe 2
      resp.aggs.filter("agg1").sum("agg2").value shouldBe 232
    }
  }
}
