package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.fields.{FloatField, IntegerField}
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.searches.aggs.responses.metrics.{TopMetric, TopMetrics}
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class TopMetricsAggregationTest extends AnyFreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("the_loop")
    }.await
  }

  client.execute {
    createIndex("the_loop") mapping {
      properties(
        FloatField("a"),
        IntegerField("b")
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("the_loop").fields("a" -> 234.45, "b"   -> 4),
      indexInto("the_loop").fields("a" -> 213.5234, "b" -> 3),
      indexInto("the_loop").fields("a" -> 9234.234, "b" -> 6),
      indexInto("the_loop").fields("a" -> 71.5, "b"     -> 5),
      indexInto("the_loop").fields("a" -> 91.5, "b"     -> 7),
      indexInto("the_loop").fields("a" -> 12.236, "b"   -> 8),
      indexInto("the_loop").fields("a" -> 8712.324, "b" -> 2)
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "top metrics aggregation" - {
    "should return metrics" in {

      val resp = client.execute {
        search("the_loop").aggs(
          topMetricsAgg("tm").withMetrics("b").withSort(fieldSort("a").desc())
        )
      }.await.result

      val agg = resp.aggs.result[TopMetrics]("tm")
      agg.name shouldBe "tm"
      agg.top shouldBe List(TopMetric(List(9234.234375), Map("b" -> 6)))
    }
    "should support size param" in {

      val resp = client.execute {
        search("the_loop").aggs(
          topMetricsAgg("tm").withMetrics("b").withSort(fieldSort("a").desc()).withSize(2)
        )
      }.await.result

      val agg = resp.aggs.result[TopMetrics]("tm")
      agg.name shouldBe "tm"
      agg.top shouldBe List(TopMetric(List(9234.234375), Map("b" -> 6)), TopMetric(List(8712.32421875), Map("b" -> 2)))
    }
  }
}
