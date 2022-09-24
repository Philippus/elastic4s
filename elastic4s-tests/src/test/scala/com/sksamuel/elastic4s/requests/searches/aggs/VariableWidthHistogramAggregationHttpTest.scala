package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.searches.aggs.responses.bucket.HistogramBucket
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class VariableWidthHistogramAggregationHttpTest extends AnyFreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("variablewidthhistaggs")
    }.await
  }

  client.execute {
    createIndex("variablewidthhistaggs") mapping {
      properties(
        textField("name").fielddata(true),
        intField("height").stored(true)
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("variablewidthhistaggs") fields("name" -> "Willis Tower",              "height" -> 1450),
      indexInto("variablewidthhistaggs") fields("name" -> "Burj Kalifa",               "height" -> 2717),
      indexInto("variablewidthhistaggs") fields("name" -> "The Shard",                 "height" -> 1017),
      indexInto("variablewidthhistaggs") fields("name" -> "One Canada Square",         "height" -> 771),
      indexInto("variablewidthhistaggs") fields("name" -> "Tower of London",           "height" -> 169),
      indexInto("variablewidthhistaggs") fields("name" -> "Shanghai Tower",            "height" -> 2073),
      indexInto("variablewidthhistaggs") fields("name" -> "Ping An Finance Centre",    "height" -> 1965),
      indexInto("variablewidthhistaggs") fields("name" -> "Abraj Al-Bait Clock Tower", "height" -> 1971)
    ).refreshImmediately
  ).await

  "variable width histogram agg" - {
    "should create histogram by field and return expected buckets with shardSize" in {

      val resp = client.execute {
        search("variablewidthhistaggs").matchAllQuery().aggs {
          variableWidthHistogramAgg("agg1", "height").buckets(3).shardSize(7)
        }
      }.await.result

      resp.totalHits shouldBe 8

      val agg = resp.aggs.histogram("agg1")
      agg.buckets.map(_.copy(data = Map.empty)) shouldBe Seq(
        HistogramBucket("470.0", 2, Map.empty),
        HistogramBucket("1233.5", 2, Map.empty),
        HistogramBucket("2181.5", 4, Map.empty)
      )
    }

    "should create histogram by field and return expected buckets with initialBuffer" in {

      val resp = client.execute {
        search("variablewidthhistaggs").matchAllQuery().aggs {
          variableWidthHistogramAgg("agg1", "height").initialBuffer(100000)
        }
      }.await.result

      val agg = resp.aggs.histogram("agg1")
      agg.buckets.map(_.copy(data = Map.empty)) shouldBe Seq(
        HistogramBucket("169.0", 1, Map.empty),
        HistogramBucket("771.0", 1, Map.empty),
        HistogramBucket("1017.0", 1, Map.empty),
        HistogramBucket("1450.0", 1, Map.empty),
        HistogramBucket("1965.0", 1, Map.empty),
        HistogramBucket("1971.0", 1, Map.empty),
        HistogramBucket("2073.0", 1, Map.empty),
        HistogramBucket("2717.0", 1, Map.empty)
      )
    }
  }
}
