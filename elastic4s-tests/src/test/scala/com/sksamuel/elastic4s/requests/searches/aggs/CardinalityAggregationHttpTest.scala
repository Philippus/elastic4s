package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class CardinalityAggregationHttpTest extends FreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("cardagg")
    }.await
  }

  client.execute {
    createIndex("cardagg") mappings {
      mapping("buildings") fields(
        textField("name").fielddata(true),
        intField("height").stored(true)
      )
    }
  }.await

  Try {
    client.execute {
      deleteIndex("cardagg2")
    }.await
  }

  client.execute {
    createIndex("cardagg2") mappings {
      mapping("buildings") fields(
        textField("name").fielddata(true),
        intField("height").stored(true)
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("cardagg/buildings") fields("name" -> "Willis Tower", "height" -> 1244),
      indexInto("cardagg/buildings") fields("name" -> "Burj Kalifa", "height" -> 2456),
      indexInto("cardagg/buildings") fields("name" -> "Tower of London", "height" -> 169)
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "cardinality agg" - {
    "should return the count of distinct values" in {

      val resp = client.execute {
        search("cardagg").matchAllQuery().aggs {
          cardinalityAgg("agg1", "name")
        }
      }.await.result

      resp.totalHits shouldBe 3

      val agg = resp.aggs.cardinality("agg1")
      // should be 6 unique terms, the 'of' in tower of london will be filtered out by the analyzer
      agg.value shouldBe 6
    }
    "should support when there are no matching doucments" in {

      val resp = client.execute {
        search("cardagg2").matchAllQuery().aggs {
          cardinalityAgg("agg1", "name")
        }
      }.await.result

      resp.totalHits shouldBe 0

      val agg = resp.aggs.cardinality("agg1")
      agg.value shouldBe 0
    }
  }
}
