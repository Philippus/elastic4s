package com.sksamuel.elastic4s.search.aggs

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.{ElasticDsl, HttpClient}
import com.sksamuel.elastic4s.testkit.SharedElasticSugar
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.scalatest.{FreeSpec, Matchers}

class TermsAggregationHttpTest extends FreeSpec with SharedElasticSugar with Matchers with ElasticDsl {

  import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._

  val http = HttpClient(ElasticsearchClientUri("elasticsearch://" + node.ipAndPort))

  http.execute {
    createIndex("termsagg") mappings {
      mapping("curry") fields(
        keywordField("name"),
        keywordField("strength")
      )
    }
  }.await

  http.execute(
    bulk(
      indexInto("termsagg/curry") fields("name" -> "Jalfrezi", "strength" -> "mild"),
      indexInto("termsagg/curry") fields("name" -> "Madras", "strength" -> "hot"),
      indexInto("termsagg/curry") fields("name" -> "Chilli masala", "strength" -> "hot"),
      indexInto("termsagg/curry") fields("name" -> "Tikka masala", "strength" -> "medium")
    ).refresh(RefreshPolicy.IMMEDIATE)
  ).await

  "terms aggregation" - {
    "should group by field" in {

      val resp = http.execute {
        search("termsagg/curry") aggs {
          termsAgg("agg1", "strength")
        }
      }.await
      resp.totalHits shouldBe 10

      val agg = resp.termsAgg("agg1")
      agg.getBuckets.size shouldBe 5
      agg.getBucketByKey("meth kingpin").getDocCount shouldBe 2
      agg.getBucketByKey("meth sidekick").getDocCount shouldBe 3
      agg.getBucketByKey("dea agent").getDocCount shouldBe 2
      agg.getBucketByKey("lawyer").getDocCount shouldBe 1
      agg.getBucketByKey("heavy").getDocCount shouldBe 2
    }

    "should only include matching documents in the query" in {
      val resp = http.execute {
        // should match 2 documents
        search("termsagg/curry") query matchQuery("name", "masala") aggregations {
          termsAgg("agg1", "job")
        }
      }.await
      resp.totalHits shouldBe 3
      val aggs = resp.termsAgg("agg1")
      aggs.getBuckets.size shouldBe 2
      aggs.getBucketByKey("dea agent").getDocCount shouldBe 2
      aggs.getBucketByKey("lawyer").getDocCount shouldBe 1
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
//    "should group by field and return a missing value" in {
//
//      val resp = client.execute {
//        search("aggregations/breakingbad").aggregations {
//          termsAggregation("agg1") field "actor" missing "no-name"
//        }
//      }.await
//      resp.totalHits shouldBe 10
//
//      val agg = resp.aggregations.stringTermsResult("agg1")
//      agg.getBuckets.size shouldBe 4
//      agg.getBucketByKey("lavell").getDocCount shouldBe 1
//      agg.getBucketByKey("bryan").getDocCount shouldBe 1
//      agg.getBucketByKey("dean").getDocCount shouldBe 1
//      agg.getBucketByKey("no-name").getDocCount shouldBe 7
//    }
  }
}
