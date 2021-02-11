package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class WeightedAvgAggregationHttpTest extends AnyFreeSpec with DockerTests with Matchers {
  Try {
    client.execute {
      deleteIndex("weightedavgagg")
    }.await
  }

  client.execute {
    createIndex("weightedavgagg") mappings {
      mapping("grades") fields(
        doubleField("grade").fielddata(true),
        doubleField("weight").stored(true)
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("weightedavgagg") fields("grade" -> 90.0, "weight" -> 3.0),
      indexInto("weightedavgagg") fields("grade" -> 72.0, "weight" -> 7.0),
      indexInto("weightedavgagg") fields("grade" -> 19.0, "weight" -> 12.0)
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "weighted avg agg" - {
    "should return the weighted avg for the context" in {

      val resp = client.execute {
        search("weightedavgagg").matchAllQuery().aggs {
          weightedAvgAgg("agg1", WeightedAvgField(field = Some("grade")), WeightedAvgField(field = Some("weight")))
        }
      }.await.result
      resp.totalHits shouldBe 3
      val agg = resp.aggs.avg("agg1")
      agg.value > 45 shouldBe true
      agg.value > 46 shouldBe false
    }
  }
}
