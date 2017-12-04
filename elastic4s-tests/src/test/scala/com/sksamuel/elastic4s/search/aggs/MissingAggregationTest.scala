package com.sksamuel.elastic4s.search.aggs

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class MissingAggregationTest extends FreeSpec with DiscoveryLocalNodeProvider with Matchers with ElasticDsl {

  Try {
    http.execute {
      deleteIndex("missingagg")
    }.await
  }

  http.execute {
    createIndex("missingagg") mappings {
      mapping("buildings") fields(
        textField("name").fielddata(true),
        intField("height").stored(true),
        intField("floors").stored(true)
      )
    }
  }.await

  http.execute(
    bulk(
      indexInto("missingagg/buildings") fields("name" -> "Willis Tower", "floors" -> 4),
      indexInto("missingagg/buildings") fields("name" -> "Burj Kalifa", "height" -> 2456),
      indexInto("missingagg/buildings") fields("name" -> "Tower of London", "floors" -> 7),
      indexInto("missingagg/buildings") fields("name" -> "London Bridge", "height" -> 63)
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "missing aggregation" - {
    "should create a bucket for docs missing the value" in {

      val resp = http.execute {
        search("missingagg").matchAllQuery().aggs {
          missingAgg("agg1", "height").subaggs {
            sumAgg("agg2", "floors")
          }
        }
      }.await.get
      resp.totalHits shouldBe 4
      resp.aggs.filter("agg1").docCount shouldBe 2
    //  resp.aggs.filter("agg1").sum("agg2").value shouldBe 11
    }
  }
}
