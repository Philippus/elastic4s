package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.mappings.FieldType.StringType
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg
import org.scalatest.{FreeSpec, Matchers}

class AggregationsTest extends FreeSpec with Matchers with ElasticSugar {

  client.execute {
    create index "aggregations" mappings {
      "breakingbad" as (
        "job" typed StringType analyzer KeywordAnalyzer
        )
    }
  }.await

  client.execute(
    bulk(
      index into "aggregations/breakingbad" fields("name" -> "walter white", "job" -> "meth kingpin", "age" -> 50),
      index into "aggregations/breakingbad" fields("name" -> "hank schrader", "job" -> "dea agent", "age" -> 55),
      index into "aggregations/breakingbad" fields("name" -> "jesse pinkman", "job" -> "meth sidekick", "age" -> 30),
      index into "aggregations/breakingbad" fields("name" -> "gus fring", "job" -> "meth kingpin", "age" -> 60),
      index into "aggregations/breakingbad" fields("name" -> "steven gomez", "job" -> "dea agent", "age" -> 50),
      index into "aggregations/breakingbad" fields("name" -> "saul goodman", "job" -> "lawyer", "age" -> 55),
      index into "aggregations/breakingbad" fields("name" -> "huell", "job" -> "heavy", "age" -> 43),
      index into "aggregations/breakingbad" fields("name" -> "mike ehrmantraut", "job" -> "heavy", "age" -> 45),
      index into "aggregations/breakingbad" fields("name" -> "lydia rodarte quayle", "job" -> "meth sidekick", "age" -> 40),
      index into "aggregations/breakingbad" fields("name" -> "todd alquist", "job" -> "meth sidekick", "age" -> 16)
    )
  ).await

  refresh("aggregations")
  blockUntilCount(10, "aggregations")

  "terms aggregation" - {
    "should group by field" in {
      val resp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          aggregation terms "agg1" field "job"
        }
      }.await
      resp.getHits.getTotalHits shouldBe 10
      val agg = resp.getAggregations.getAsMap.get("agg1").asInstanceOf[StringTerms]
      agg.getBuckets.size shouldBe 5
      agg.getBucketByKey("meth kingpin").getDocCount shouldBe 2
      agg.getBucketByKey("meth sidekick").getDocCount shouldBe 3
      agg.getBucketByKey("dea agent").getDocCount shouldBe 2
      agg.getBucketByKey("lawyer").getDocCount shouldBe 1
      agg.getBucketByKey("heavy").getDocCount shouldBe 2
    }
    "should only include matching documents in the query" in {
      val resp = client.execute {
        // should match 3 documents
        search in "aggregations/breakingbad" query prefixQuery("name" -> "s") aggregations {
          aggregation terms "agg1" field "job"
        }
      }.await
      resp.getHits.getTotalHits shouldBe 3
      val aggs = resp.getAggregations.getAsMap.get("agg1").asInstanceOf[StringTerms]
      aggs.getBuckets.size shouldBe 2
      aggs.getBucketByKey("dea agent").getDocCount shouldBe 2
      aggs.getBucketByKey("lawyer").getDocCount shouldBe 1
    }
  }

  "avg aggregation" - {
    "should average by field" in {
      val resp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          aggregation avg "agg1" field "age"
        }
      }.await
      resp.getHits.getTotalHits shouldBe 10
      val agg = resp.getAggregations.getAsMap.get("agg1").asInstanceOf[InternalAvg]
      agg.getValue shouldBe 44.4
    }
    "should only include matching documents in the query" in {
      val resp = client.execute {
        // should match 3 documents
        search in "aggregations/breakingbad" query prefixQuery("name" -> "g") aggregations {
          aggregation avg "agg1" field "age"
        }
      }.await
      resp.getHits.getTotalHits shouldBe 3
      val agg = resp.getAggregations.getAsMap.get("agg1").asInstanceOf[InternalAvg]
      agg.getValue shouldBe 55
    }
  }
}
