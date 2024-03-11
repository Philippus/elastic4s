package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class PercentilesAggregationTest extends AnyFreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("percentilesagg")
    }.await
  }

  client.execute {
    createIndex("percentilesagg").mapping(
      properties(
        textField("name").fielddata(true),
        intField("height").stored(true)
      )
    )
  }.await

  client.execute(
    bulk(
      indexInto("percentilesagg") fields("name" -> "Willis Tower", "height" -> 1450),
      indexInto("percentilesagg") fields("name" -> "Burj Kalifa", "height" -> 2717),
      indexInto("percentilesagg") fields("name" -> "The Shard", "height" -> 1017),
      indexInto("percentilesagg") fields("name" -> "One Canada Square", "height" -> 771),
      indexInto("percentilesagg") fields("name" -> "Tower of London", "height" -> 169),
      indexInto("percentilesagg") fields("name" -> "Shanghai Tower", "height" -> 2073),
      indexInto("percentilesagg") fields("name" -> "Ping An Finance Centre", "height" -> 1965),
      indexInto("percentilesagg") fields("name" -> "Abraj Al-Bait Clock Tower", "height" -> 1971)
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "percentiles agg" - {
    "should return the percentiles the matching results" in {
      val resp = client.execute {
        search("percentilesagg").matchAllQuery().aggs {
          percentilesAgg("agg1", "height")
        }
      }.await.result
      resp.totalHits shouldBe 8
      val agg = resp.aggs.percentiles("agg1")
      agg.values shouldBe Map("99.0" -> 2671.92, "25.0" -> 955.5, "95.0" -> 2491.5999999999995, "50.0" -> 1707.5, "75.0" -> 1996.5, "1.0" -> 211.14, "5.0" -> 379.70000000000005)
    }
    "should allow setting which percentiles to return" in {
      val resp = client.execute {
        search("percentilesagg").matchAllQuery().aggs {
          percentilesAgg("agg1", "height").percents(50, 80)
        }
      }.await.result
      resp.totalHits shouldBe 8
      val agg = resp.aggs.percentiles("agg1")
      agg.values shouldBe Map("50.0" -> 1707.5, "80.0" -> 2032.2)
    }
    "should support keyed=false" in {
      val resp = client.execute {
        search("percentilesagg").matchAllQuery().aggs {
          percentilesAgg("agg1", "height").percents(50, 80).keyed(false)
        }
      }.await.result
      resp.totalHits shouldBe 8
      val agg = resp.aggs.percentiles("agg1")
      agg.values shouldBe Map("50.0" -> 1707.5, "80.0" -> 2032.2)
    }
  }
}
