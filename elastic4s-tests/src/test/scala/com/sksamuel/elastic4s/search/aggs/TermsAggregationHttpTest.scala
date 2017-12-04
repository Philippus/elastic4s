package com.sksamuel.elastic4s.search.aggs

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.http.search.TermBucket
import com.sksamuel.elastic4s.searches.aggs.TermsOrder
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class TermsAggregationHttpTest extends FreeSpec with DiscoveryLocalNodeProvider with Matchers with ElasticDsl {

  Try {
    http.execute {
      deleteIndex("termsagg")
    }.await
  }

  http.execute {
    createIndex("termsagg") mappings {
      mapping("curry") fields(
        textField("name").fielddata(true),
        textField("strength").fielddata(true).stored(true),
        keywordField("origin")
      )
    }
  }.await

  http.execute(
    bulk(
      indexInto("termsagg/curry") fields("name" -> "Jalfrezi", "strength" -> "mild", "origin" -> "india"),
      indexInto("termsagg/curry") fields("name" -> "Madras", "strength" -> "hot", "origin" -> "india"),
      indexInto("termsagg/curry") fields("name" -> "Chilli Masala", "strength" -> "hot", "origin" -> "india"),
      indexInto("termsagg/curry") fields("name" -> "Tikka Masala", "strength" -> "medium")
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "terms aggregation" - {
    "should group by field" in {

      val resp = http.execute {
        search("termsagg/curry").matchAllQuery().aggs {
          termsAgg("agg1", "strength")
        }
      }.await.get
      resp.totalHits shouldBe 4

      val agg = resp.aggregations.terms("agg1")
      agg.buckets.map(_.copy(data = Map.empty)).toSet shouldBe Set(TermBucket("hot", 2, Map.empty), TermBucket("medium", 1, Map.empty), TermBucket("mild", 1, Map.empty))
    }

    "should only include matching documents in the query" in {
      val resp = http.execute {
        // should match 2 documents
        search("termsagg/curry").matchQuery("name", "masala").aggregations {
          termsAgg("agg1", "strength")
        }
      }.await.get
      resp.size shouldBe 2

      val agg = resp.aggregations.terms("agg1")
      agg.buckets.map(_.copy(data = Map.empty)).toSet shouldBe Set(TermBucket("hot", 1, Map.empty), TermBucket("medium", 1, Map.empty))
    }

    "should support missing value" in {

      val resp = http.execute {
        search("termsagg/curry").aggregations {
          termsAggregation("agg1") field "origin" missing "unknown"
        }
      }.await.get
      resp.totalHits shouldBe 4

      val agg = resp.aggs.terms("agg1")
      agg.buckets.map(_.copy(data = Map.empty)).toSet shouldBe Set(TermBucket("india", 3, Map.empty), TermBucket("unknown", 1, Map.empty))
    }

    "should support min doc count" in {

      val resp = http.execute {
        search("termsagg/curry").aggregations {
          termsAggregation("agg1") field "strength" minDocCount 2
        }
      }.await.get
      resp.totalHits shouldBe 4

      val agg = resp.aggs.terms("agg1")
      agg.buckets.map(_.copy(data = Map.empty)).toSet shouldBe Set(TermBucket("hot", 2, Map.empty))
    }

    "should support size" in {

      val resp = http.execute {
        search("termsagg/curry").aggregations {
          termsAggregation("agg1") field "strength" size 1
        }
      }.await.get
      resp.totalHits shouldBe 4

      val agg = resp.aggs.terms("agg1")
      agg.buckets.map(_.copy(data = Map.empty)).toSet shouldBe Set(TermBucket("hot", 2, Map.empty))
    }

    "should support sub aggregations" in {

      val resp = http.execute {
        search("termsagg/curry").matchAllQuery().aggs {
          termsAgg("agg1", "strength").addSubagg(
            termsAgg("agg2", "origin")
          )
        }
      }.await.get
      resp.totalHits shouldBe 4

      val agg = resp.aggregations.terms("agg1")
      agg.bucket("hot").terms("agg2").buckets.map(_.copy(data = Map.empty)).toSet shouldBe Set(TermBucket("india", 2, Map.empty))
    }

    "should support _count desc terms order" in {
      val resp = http.execute {
        search("termsagg/curry").matchAllQuery().aggs {
          termsAgg("agg1", "strength").order(TermsOrder("_count", false))
        }
      }.await.get

      val agg = resp.aggregations.terms("agg1")
      agg.buckets.map(_.key) shouldBe List("hot", "medium", "mild")
    }

    "should support _count asc terms order" in {
      val resp = http.execute {
        search("termsagg/curry").matchAllQuery().aggs {
          termsAgg("agg1", "strength").order(TermsOrder("_count", true))
        }
      }.await.get

      val agg = resp.aggregations.terms("agg1")
      agg.buckets.map(_.key) shouldBe List("medium", "mild", "hot")
    }
  }
}
