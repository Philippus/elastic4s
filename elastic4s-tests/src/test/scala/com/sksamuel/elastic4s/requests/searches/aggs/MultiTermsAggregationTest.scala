package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.searches.aggs.responses.bucket.{MultiTerms, MultiTermBucket, TermBucket, Terms}
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class MultiTermsAggregationTest extends AnyFreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("multitermsagg")
    }.await
  }

  client.execute {
    createIndex("multitermsagg") mapping {
      properties(
        textField("name").fielddata(true),
        textField("strength").fielddata(true).stored(true),
        keywordField("origin")
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("multitermsagg") fields ("name" -> "Jalfrezi", "strength"      -> "mild", "origin" -> "india"),
      indexInto("multitermsagg") fields ("name" -> "Madras", "strength"        -> "hot", "origin"  -> "india"),
      indexInto("multitermsagg") fields ("name" -> "Chilli Masala", "strength" -> "hot", "origin"  -> "india"),
      indexInto("multitermsagg") fields ("name" -> "Tikka Masala", "strength"  -> "medium")
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "terms aggregation" - {
    "should group by fields" in {

      val resp = client.execute {
        search("multitermsagg").matchAllQuery().aggs {
          multiTermsAgg(
            "agg1",
            MultiTermsAggregation.Term().field("strength"),
            MultiTermsAggregation.Term().field("origin")
          )
        }
      }.await.result
      resp.totalHits shouldBe 4

      val agg = resp.aggregations.result[MultiTerms]("agg1")
      agg.buckets.map(_.copy(data = Map.empty)).toSet shouldBe Set(
        MultiTermBucket(List("hot", "india"), 2, Map.empty),
        MultiTermBucket(List("mild", "india"), 1, Map.empty)
      )
    }

    "should only include matching documents in the query" in {
      val resp = client.execute {
        // should match 2 documents
        search("multitermsagg").matchQuery("name", "masala").aggregations {
          multiTermsAgg(
            "agg1",
            MultiTermsAggregation.Term().field("strength"),
            MultiTermsAggregation.Term().field("origin")
          )
        }
      }.await.result
      resp.size shouldBe 2

      val agg = resp.aggregations.result[MultiTerms]("agg1")
      agg.buckets.map(_.copy(data = Map.empty)).toSet shouldBe Set(MultiTermBucket(List("hot", "india"), 1, Map.empty))
    }

    "should support missing value" in {

      val resp = client.execute {
        search("multitermsagg").aggregations {
          multiTermsAgg(
            "agg1",
            MultiTermsAggregation.Term().field("strength"),
            MultiTermsAggregation.Term().field("origin").missing("unknown")
          )
        }
      }.await.result
      resp.totalHits shouldBe 4

      val agg = resp.aggs.result[MultiTerms]("agg1")
      agg.buckets.map(_.copy(data = Map.empty)).toSet shouldBe Set(
        MultiTermBucket(List("hot", "india"), 2, Map.empty),
        MultiTermBucket(List("medium", "unknown"), 1, Map.empty),
        MultiTermBucket(List("mild", "india"), 1, Map.empty)
      )
    }

    "should support min doc count" in {

      val resp = client.execute {
        search("multitermsagg").aggregations {
          multiTermsAgg(
            "agg1",
            MultiTermsAggregation.Term().field("strength"),
            MultiTermsAggregation.Term().field("origin")
          ) minDocCount 2
        }
      }.await.result
      resp.totalHits shouldBe 4

      val agg = resp.aggs.result[MultiTerms]("agg1")
      agg.buckets.map(_.copy(data = Map.empty)).toSet shouldBe Set(MultiTermBucket(List("hot", "india"), 2, Map.empty))
    }

    "should support size" in {

      val resp = client.execute {
        search("multitermsagg").aggregations {
          multiTermsAgg(
            "agg1",
            MultiTermsAggregation.Term().field("strength"),
            MultiTermsAggregation.Term().field("origin")
          ) size 1
        }
      }.await.result
      resp.totalHits shouldBe 4

      val agg = resp.aggs.result[MultiTerms]("agg1")
      agg.buckets.map(_.copy(data = Map.empty)).toSet shouldBe Set(MultiTermBucket(List("hot", "india"), 2, Map.empty))
    }

    "should support sub aggregations" in {

      val resp = client.execute {
        search("multitermsagg").matchAllQuery().aggs {
          multiTermsAgg(
            "agg1",
            MultiTermsAggregation.Term().field("strength"),
            MultiTermsAggregation.Term().field("origin")
          ).addSubagg(
            termsAgg("agg2", "name")
          )
        }
      }.await.result
      resp.totalHits shouldBe 4

      val agg = resp.aggregations.result[MultiTerms]("agg1")
      agg.bucket(List("hot", "india").toSeq).result[Terms]("agg2").buckets.map(_.copy(data = Map.empty)).toSet shouldBe
        Set(TermBucket("chilli", 1, Map.empty), TermBucket("madras", 1, Map.empty), TermBucket("masala", 1, Map.empty))
    }

    "should support _count desc terms order" in {
      val resp = client.execute {
        search("multitermsagg").matchAllQuery().aggs {
          multiTermsAgg(
            "agg1",
            MultiTermsAggregation.Term().field("strength"),
            MultiTermsAggregation.Term().field("origin")
          ).order(TermsOrder("_count", false))
        }
      }.await.result

      val agg = resp.aggregations.result[MultiTerms]("agg1")
      agg.buckets.map(_.key) shouldBe List(List("hot", "india"), List("mild", "india"))
    }

    "should support _count asc terms order" in {
      val resp = client.execute {
        search("multitermsagg").matchAllQuery().aggs {
          multiTermsAgg(
            "agg1",
            MultiTermsAggregation.Term().field("strength"),
            MultiTermsAggregation.Term().field("origin")
          ).order(TermsOrder("_count", true))
        }
      }.await.result

      val agg = resp.aggregations.result[MultiTerms]("agg1")
      agg.buckets.map(_.key) shouldBe List(List("mild", "india"), List("hot", "india"))
    }

    "should support multi criteria order" in {
      val resp = client.execute {
        search("multitermsagg").matchAllQuery().aggs {
          multiTermsAgg(
            "agg1",
            MultiTermsAggregation.Term().field("strength"),
            MultiTermsAggregation.Term().field("origin")
          ).order(TermsOrder("_count", true), TermsOrder("_key", false))
        }
      }.await.result

      val agg = resp.aggregations.result[MultiTerms]("agg1")
      agg.buckets.map(_.key) shouldBe List(List("mild", "india"), List("hot", "india"))
    }
  }
}
