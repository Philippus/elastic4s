package com.sksamuel.elastic4s.search.aggs

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class PercentilesAggregationHttpTest extends FreeSpec with DiscoveryLocalNodeProvider with Matchers with ElasticDsl {

  Try {
    http.execute {
      deleteIndex("percentilesagg")
    }.await
  }

  http.execute {
    createIndex("percentilesagg") mappings {
      mapping("buildings") fields(
        textField("name").fielddata(true),
        intField("height").stored(true)
      )
    }
  }.await

  http.execute(
    bulk(
      indexInto("percentilesagg/buildings") fields("name" -> "Willis Tower", "height" -> 1450),
      indexInto("percentilesagg/buildings") fields("name" -> "Burj Kalifa", "height" -> 2717),
      indexInto("percentilesagg/buildings") fields("name" -> "The Shard", "height" -> 1017),
      indexInto("percentilesagg/buildings") fields("name" -> "One Canada Square", "height" -> 771),
      indexInto("percentilesagg/buildings") fields("name" -> "Tower of London", "height" -> 169),
      indexInto("percentilesagg/buildings") fields("name" -> "Shanghai Tower", "height" -> 2073),
      indexInto("percentilesagg/buildings") fields("name" -> "Ping An Finance Centre", "height" -> 1965),
      indexInto("percentilesagg/buildings") fields("name" -> "Abraj Al-Bait Clock Tower", "height" -> 1971)
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "percentiles agg" - {
    "should return the percentiles the matching results" in {
      val resp = http.execute {
        search("percentilesagg").matchAllQuery().aggs {
          percentilesAgg("agg1", "height")
        }
      }.await.right.get.result
      resp.totalHits shouldBe 8
      val agg = resp.aggs.percentiles("agg1")
      agg.values shouldBe Map("99.0" -> 2671.9199999999996, "25.0" -> 955.5, "95.0" -> 2491.5999999999995, "50.0" -> 1707.5, "75.0" -> 1996.5, "1.0" -> 211.14, "5.0" -> 379.7)
    }
    "should allow setting which percentiles to return" in {
      val resp = http.execute {
        search("percentilesagg").matchAllQuery().aggs {
          percentilesAgg("agg1", "height").percents(50, 80)
        }
      }.await.right.get.result
      resp.totalHits shouldBe 8
      val agg = resp.aggs.percentiles("agg1")
      agg.values shouldBe Map("50.0" -> 1707.5, "80.0" -> 2032.2)
    }
  }
}
