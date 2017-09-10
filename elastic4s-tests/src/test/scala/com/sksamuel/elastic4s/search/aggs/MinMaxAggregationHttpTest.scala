package com.sksamuel.elastic4s.search.aggs

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class MinMaxAggregationHttpTest extends FreeSpec with DiscoveryLocalNodeProvider with Matchers with ElasticDsl {

  Try {
    http.execute {
      deleteIndex("minmaxagg")
    }.await
  }

  http.execute {
    createIndex("minmaxagg") mappings {
      mapping("buildings") fields(
        textField("name").fielddata(true),
        intField("height").stored(true)
      )
    }
  }.await

  http.execute(
    bulk(
      indexInto("minmaxagg/buildings") fields("name" -> "Willis Tower", "height" -> 1244),
      indexInto("minmaxagg/buildings") fields("name" -> "Burj Kalifa", "height" -> 2456),
      indexInto("minmaxagg/buildings") fields("name" -> "Tower of London", "height" -> 169)
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "max agg" - {
    "should return the max for the context" in {

      val resp = http.execute {
        search("minmaxagg").matchAllQuery().aggs {
          maxAgg("agg1", "height")
        }
      }.await.right.get
      resp.totalHits shouldBe 3
      val agg = resp.aggs.max("agg1")
      agg.value shouldBe 2456
    }
  }

  "min agg" - {
    "should return the max for the context" in {

      val resp = http.execute {
        search("minmaxagg").matchAllQuery().aggs {
          minAgg("agg1", "height")
        }
      }.await.right.get
      resp.totalHits shouldBe 3
      val agg = resp.aggs.min("agg1")
      agg.value shouldBe 169
    }
  }
}
