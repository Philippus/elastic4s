package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class AvgAggregationHttpTest extends AnyFreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("avgagg")
    }.await
  }

  client.execute {
    createIndex("avgagg") mappings {
      mapping(
        textField("name").fielddata(true),
        intField("height").stored(true)
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("avgagg") fields("name" -> "Willis Tower", "height" -> 1244),
      indexInto("avgagg") fields("name" -> "Burj Kalifa", "height" -> 2456),
      indexInto("avgagg") fields("name" -> "Tower of London", "height" -> 169)
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "avg agg" - {
    "should return the avg for the context" in {

      val resp = client.execute {
        search("avgagg").matchAllQuery().aggs {
          avgAgg("agg1", "height")
        }
      }.await.result
      resp.totalHits shouldBe 3
      val agg = resp.aggs.avg("agg1")
      agg.value > 1289 shouldBe true
      agg.value > 1290 shouldBe false
    }
  }
}
