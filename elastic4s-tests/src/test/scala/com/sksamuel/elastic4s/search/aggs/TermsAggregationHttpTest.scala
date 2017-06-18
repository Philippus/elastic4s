package com.sksamuel.elastic4s.search.aggs

import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.http.search.TermBucket
import com.sksamuel.elastic4s.testkit.DiscoveryLocalNodeProvider
import org.scalatest.{FreeSpec, Matchers}

class TermsAggregationHttpTest extends FreeSpec with DiscoveryLocalNodeProvider with Matchers with ElasticDsl {

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
      }.await
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
      }.await
      resp.size shouldBe 2

      val agg = resp.aggregations.terms("agg1")
      agg.buckets.map(_.copy(data = Map.empty)).toSet shouldBe Set(TermBucket("hot", 1, Map.empty), TermBucket("medium", 1, Map.empty))
    }

    "should support missing value" in {

      val resp = http.execute {
        search("termsagg/curry").aggregations {
          termsAggregation("agg1") field "origin" missing "unknown"
        }
      }.await
      resp.totalHits shouldBe 4

      val agg = resp.aggs.terms("agg1")
      agg.buckets.map(_.copy(data = Map.empty)).toSet shouldBe Set(TermBucket("india", 3, Map.empty), TermBucket("unknown", 1, Map.empty))
    }

    "should support min doc count" in {

      val resp = http.execute {
        search("termsagg/curry").aggregations {
          termsAggregation("agg1") field "strength" minDocCount 2
        }
      }.await
      resp.totalHits shouldBe 4

      val agg = resp.aggs.terms("agg1")
      agg.buckets.map(_.copy(data = Map.empty)).toSet shouldBe Set(TermBucket("hot", 2, Map.empty))
    }

    "should support size" in {

      val resp = http.execute {
        search("termsagg/curry").aggregations {
          termsAggregation("agg1") field "strength" size 1
        }
      }.await
      resp.totalHits shouldBe 4

      val agg = resp.aggs.terms("agg1")
      agg.buckets.map(_.copy(data = Map.empty)).toSet shouldBe Set(TermBucket("hot", 2, Map.empty))
    }

    "should support sub aggregations" in {

      val resp = http.execute {
        search("termsagg/curry").matchAllQuery().aggs {
          termsAgg("agg1", "strength").subagg(
            termsAgg("agg2", "origin")
          )
        }
      }.await
      resp.totalHits shouldBe 4

      val agg = resp.aggregations.terms("agg1")
      agg.bucket("hot").terms("agg2").buckets.map(_.copy(data = Map.empty)).toSet shouldBe Set(TermBucket("india", 2, Map.empty))
    }

    //    "should only return included fields" in {
    //      val resp = client.execute {
    //        search("aggregations/breakingbad") aggregations {
    //          termsAggregation("agg1") field "job" includeExclude("lawyer", "")
    //        }
    //      }.await
    //      resp.totalHits shouldBe 10
    //      val agg = resp.aggregations.map("agg1").asInstanceOf[StringTerms]
    //      agg.getBuckets.size shouldBe 1
    //      agg.getBucketByKey("lawyer").getDocCount shouldBe 1
    //    }
    //
    //    "should not return excluded fields" in {
    //      val resp = client.execute {
    //        search("aggregations/breakingbad") aggregations {
    //          termsAggregation("agg1") field "job" includeExclude("", "lawyer")
    //        }
    //      }.await
    //      resp.totalHits shouldBe 10
    //
    //
    //      val agg = resp.aggregations.stringTermsResult("agg1")
    //      agg.getBuckets.size shouldBe 4
    //      agg.getBucketByKey("meth sidekick").getDocCount shouldBe 3
    //      agg.getBucketByKey("meth kingpin").getDocCount shouldBe 2
    //      agg.getBucketByKey("dea agent").getDocCount shouldBe 2
    //      agg.getBucketByKey("heavy").getDocCount shouldBe 2
    //    }
    //
    //    "should only return included fields (given a seq)" in {
    //      val resp = client.execute {
    //        search("aggregations/breakingbad") aggregations {
    //          termsAggregation("agg1") field "job" includeExclude(Seq("meth kingpin", "lawyer"), Nil)
    //        }
    //      }.await
    //      resp.totalHits shouldBe 10
    //      val agg = resp.aggregations.map("agg1").asInstanceOf[StringTerms]
    //      agg.getBuckets.size shouldBe 2
    //      agg.getBucketByKey("meth kingpin").getDocCount shouldBe 2
    //      agg.getBucketByKey("lawyer").getDocCount shouldBe 1
    //    }
    //
    //    "should not return excluded fields (given a seq)" in {
    //      val resp = client.execute {
    //        search("aggregations/breakingbad") aggregations {
    //          termsAggregation("agg1") field "job" includeExclude(Nil, Iterable("lawyer"))
    //        }
    //      }.await
    //      resp.totalHits shouldBe 10
    //
    //      val agg = resp.aggregations.stringTermsResult("agg1")
    //      agg.getBuckets.size shouldBe 4
    //      agg.getBucketByKey("meth sidekick").getDocCount shouldBe 3
    //      agg.getBucketByKey("meth kingpin").getDocCount shouldBe 2
    //      agg.getBucketByKey("dea agent").getDocCount shouldBe 2
    //      agg.getBucketByKey("heavy").getDocCount shouldBe 2
    //    }
    //

  }
}
