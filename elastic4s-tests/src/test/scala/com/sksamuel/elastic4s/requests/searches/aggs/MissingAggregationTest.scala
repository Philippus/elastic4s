package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class MissingAggregationTest extends FreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("missingagg")
    }.await
  }

  client.execute {
    createIndex("missingagg") mappings {
      mapping("buildings") fields(
        textField("name").fielddata(true),
        intField("height").stored(true),
        intField("floors").stored(true)
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("missingagg/buildings") fields("name" -> "Willis Tower", "floors" -> 4),
      indexInto("missingagg/buildings") fields("name" -> "Burj Kalifa", "height" -> 2456),
      indexInto("missingagg/buildings") fields("name" -> "Tower of London", "floors" -> 7),
      indexInto("missingagg/buildings") fields("name" -> "London Bridge", "height" -> 63)
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "missing aggregation" - {
    "should create a bucket for docs missing the value" in {

      val resp = client.execute {
        search("missingagg").matchAllQuery().aggs {
          missingAgg("agg1", "height").subaggs {
            sumAgg("agg2", "floors")
          }
        }
      }.await.result
      resp.totalHits shouldBe 4
      resp.aggs.filter("agg1").docCount shouldBe 2
    //  resp.aggs.filter("agg1").sum("agg2").value shouldBe 11
    }
  }
}
