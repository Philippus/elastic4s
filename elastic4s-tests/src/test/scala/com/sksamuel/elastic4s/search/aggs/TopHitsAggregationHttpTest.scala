package com.sksamuel.elastic4s.search.aggs

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.testkit.{DiscoveryLocalNodeProvider, DockerTests}
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class TopHitsAggregationHttpTest extends FreeSpec with DockerTests with Matchers {

  Try {
    http.execute {
      deleteIndex("tophits")
    }.await
  }

  http.execute {
    createIndex("tophits") mappings {
      mapping("landmarks") fields(
        textField("name").fielddata(true),
        textField("location").fielddata(true)
      )
    }
  }.await

  http.execute(
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

      val resp = http.execute {
        search("tophits/landmarks").matchAllQuery().aggs {
          termsAgg("agg1", "location").addSubagg(
            topHitsAgg("agg2").sortBy(fieldSort("name"))
          )
        }
      }.await.right.get.result
      resp.totalHits shouldBe 5

      val agg = resp.aggs.terms("agg1")
      val tophits = agg.buckets.find(_.key == "london").get.tophits("agg2")
      tophits.total shouldBe 3
      tophits.maxScore shouldBe None
      tophits.name shouldBe "agg2"
      tophits.hits.head.index shouldBe "tophits"
      tophits.hits.head.source shouldBe Map("name" -> "buckingham palace", "location" -> "london")
      tophits.hits.head.sort shouldBe Seq("buckingham")

    }
  }
}
