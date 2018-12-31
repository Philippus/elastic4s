package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.AggReader
import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.searches.Total
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class TopHitsAggregationTest extends FreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("tophits")
    }.await
  }

  client.execute {
    createIndex("tophits") mappings {
      mapping("landmarks") fields(
        textField("name").fielddata(true),
        textField("location").fielddata(true)
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("tophits/landmarks") fields("name" -> "tower of london", "location" -> "london"),
      indexInto("tophits/landmarks") fields("name" -> "buckingham palace", "location" -> "london"),
      indexInto("tophits/landmarks") fields("name" -> "hampton court palace", "location" -> "london"),
      indexInto("tophits/landmarks") fields("name" -> "york minster", "location" -> "yorkshire"),
      indexInto("tophits/landmarks") fields("name" -> "stonehenge", "location" -> "wiltshire")
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "top hits aggregation" - {
    "should be useable as a sub agg" in {

      val resp = client.execute {
        search("tophits/landmarks").matchAllQuery().aggs {
          termsAgg("agg1", "location").addSubagg(
            topHitsAgg("agg2").sortBy(fieldSort("name"))
          )
        }
      }.await.result
      resp.totalHits shouldBe 5

      val agg = resp.aggs.terms("agg1")
      val tophits = agg.buckets.find(_.key == "london").get.tophits("agg2")
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
      val resp = client.execute {
        search("tophits/landmarks").matchAllQuery().aggs {
          termsAgg("agg1", "location").addSubagg(
            topHitsAgg("agg2").sortBy(fieldSort("name"))
          )
        }
      }.await.result
      resp.totalHits shouldBe 5

      val agg = resp.aggs.terms("agg1")
      val tophits = agg.buckets.find(_.key == "london").get.tophits("agg2")
      tophits.hits.head.safeTo[String].get shouldBe "{\"name\":\"buckingham palace\",\"location\":\"london\"}"

    }
  }
}
