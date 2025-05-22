package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.AggReader
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.searches.{HighlightField, Total}
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import scala.util.Try

import com.sksamuel.elastic4s.requests.searches.aggs.responses.bucket.Terms
import com.sksamuel.elastic4s.requests.searches.aggs.responses.metrics.TopHits

class TopHitsAggregationTest extends AnyFreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("tophits")
    }.await
  }

  client.execute {
    createIndex("tophits") mapping {
      properties(
        textField("name").fielddata(true),
        textField("location").fielddata(true)
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("tophits") fields ("name" -> "tower of london", "location"      -> "london"),
      indexInto("tophits") fields ("name" -> "buckingham palace", "location"    -> "london"),
      indexInto("tophits") fields ("name" -> "hampton court palace", "location" -> "london"),
      indexInto("tophits") fields ("name" -> "york minster", "location"         -> "yorkshire"),
      indexInto("tophits") fields ("name" -> "stonehenge", "location"           -> "wiltshire")
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "top hits aggregation" - {
    "should be useable as a sub agg" in {

      val resp = client.execute {
        search("tophits").matchAllQuery().aggs {
          termsAgg("agg1", "location").addSubagg(
            topHitsAgg("agg2").sortBy(fieldSort("name"))
          )
        }
      }.await.result
      resp.totalHits shouldBe 5

      val agg     = resp.aggs.result[Terms]("agg1")
      val tophits = agg.buckets.find(_.key == "london").get.result[TopHits]("agg2")
      tophits.total shouldBe Total(3, "eq")
      tophits.maxScore shouldBe None
      tophits.name shouldBe "agg2"
      tophits.hits.head.index shouldBe "tophits"
      tophits.hits.head.source shouldBe Map("name" -> "buckingham palace", "location" -> "london")
      tophits.hits.head.sort shouldBe Seq("buckingham")

    }
    "should convert TopHit to given type" in {

      implicit val stringReader: AggReader[String] = new AggReader[String] {
        override def read(json: String): Try[String] = Try(json)
      }
      val resp                                     = client.execute {
        search("tophits").matchAllQuery().aggs {
          termsAgg("agg1", "location").addSubagg(
            topHitsAgg("agg2").sortBy(fieldSort("name"))
          )
        }
      }.await.result
      resp.totalHits shouldBe 5

      val agg     = resp.aggs.result[Terms]("agg1")
      val tophits = agg.buckets.find(_.key == "london").get.result[TopHits]("agg2")
      tophits.hits.head.safeTo[String].get shouldBe "{\"name\":\"buckingham palace\",\"location\":\"london\"}"
    }

    "should support highlighting" in {
      val resp = client.execute {
        search("tophits").matchQuery("name", "palace").aggs {
          termsAgg("agg1", "location").addSubagg(
            topHitsAgg("agg2").sortBy(fieldSort("name")).highlighting(HighlightField("name"))
          )
        }
      }.await.result

      val tophits = resp.aggs.result[Terms]("agg1").buckets.find(_.key == "london").get.result[TopHits]("agg2")
      tophits.hits.head.highlight shouldBe Map("name" -> List("buckingham <em>palace</em>"))
    }
  }
}
