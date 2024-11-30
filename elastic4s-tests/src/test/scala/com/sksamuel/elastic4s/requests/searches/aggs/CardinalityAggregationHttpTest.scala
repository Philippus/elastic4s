package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class CardinalityAggregationHttpTest extends AnyFreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("cardagg")
    }.await
  }

  client.execute {
    createIndex("cardagg") mapping {
      properties(
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
    createIndex("cardagg2") mapping {
      properties(
        textField("name").fielddata(true),
        intField("height").stored(true)
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("cardagg") fields ("name" -> "Willis Tower", "height"    -> 1244),
      indexInto("cardagg") fields ("name" -> "Burj Kalifa", "height"     -> 2456),
      indexInto("cardagg") fields ("name" -> "Tower of London", "height" -> 169)
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
