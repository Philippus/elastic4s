package com.sksamuel.elastic4s.search.aggs

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class AvgAggregationHttpTest extends FreeSpec with DiscoveryLocalNodeProvider with Matchers with ElasticDsl {

  Try {
    http.execute {
      deleteIndex("avgagg")
    }.await
  }

  http.execute {
    createIndex("avgagg") mappings {
      mapping("buildings") fields(
        textField("name").fielddata(true),
        intField("height").stored(true)
      )
    }
  }.await

  http.execute(
    bulk(
      indexInto("avgagg/buildings") fields("name" -> "Willis Tower", "height" -> 1244),
      indexInto("avgagg/buildings") fields("name" -> "Burj Kalifa", "height" -> 2456),
      indexInto("avgagg/buildings") fields("name" -> "Tower of London", "height" -> 169)
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "avg agg" - {
    "should return the avg for the context" in {

      val resp = http.execute {
        search("avgagg").matchAllQuery().aggs {
          avgAgg("agg1", "height")
        }
      }.await
      resp.totalHits shouldBe 3
      val agg = resp.aggs.avg("agg1")
      agg.value > 1289 shouldBe true
      agg.value > 1290 shouldBe false
    }
  }
}
