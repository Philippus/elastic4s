package com.sksamuel.elastic4s.search.aggs

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.search.Bucket
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{FreeSpec, Matchers}

class TermsAggregationHttpTest extends FreeSpec with SharedElasticSugar with Matchers with ElasticDsl {

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

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
    ).refresh(RefreshPolicy.IMMEDIATE)
  ).await

  "terms aggregation" - {
    "should group by field" ignore {

      val resp = http.execute {
        search("termsagg/curry").matchAllQuery().aggs {
          termsAgg("agg1", "strength")
        }
      }.await
      resp.totalHits shouldBe 4

      val agg = resp.termsAgg("agg1")
      agg.buckets.toSet shouldBe Set(Bucket("hot", 2), Bucket("medium", 1), Bucket("mild", 1))
    }

    "should only include matching documents in the query" ignore {
      val resp = http.execute {
        // should match 2 documents
        search("termsagg/curry").matchQuery("name", "masala").aggregations {
          termsAgg("agg1", "strength")
        }
      }.await
      resp.size shouldBe 2

      val agg = resp.termsAgg("agg1")
      agg.buckets.toSet shouldBe Set(Bucket("hot", 1), Bucket("medium", 1))
    }

    "should support missing value" ignore {

      val resp = http.execute {
        search("termsagg/curry").aggregations {
          termsAggregation("agg1") field "origin" missing "unknown"
        }
      }.await
      resp.totalHits shouldBe 4

      val agg = resp.termsAgg("agg1")
      agg.buckets.toSet shouldBe Set(Bucket("india", 3), Bucket("unknown", 1))
    }

    "should support min doc count" ignore {

      val resp = http.execute {
        search("termsagg/curry").aggregations {
          termsAggregation("agg1") field "strength" minDocCount 2
        }
      }.await
      resp.totalHits shouldBe 4

      val agg = resp.termsAgg("agg1")
      agg.buckets.toSet shouldBe Set(Bucket("hot", 2))
    }

    "should support size" ignore {

      val resp = http.execute {
        search("termsagg/curry").aggregations {
          termsAggregation("agg1") field "strength" size 1
        }
      }.await
      resp.totalHits shouldBe 4

      val agg = resp.termsAgg("agg1")
      agg.buckets.toSet shouldBe Set(Bucket("hot", 2))
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
