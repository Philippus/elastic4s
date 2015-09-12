package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.anaylzers.KeywordAnalyzer
import com.sksamuel.elastic4s.mappings.FieldType.StringType
import org.elasticsearch.search.aggregations.bucket.missing.InternalMissing
import org.elasticsearch.search.aggregations.bucket.range.InternalRange
import org.elasticsearch.search.aggregations.bucket.range.InternalRange.Bucket
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg
import org.elasticsearch.search.aggregations.metrics.cardinality.InternalCardinality
import org.elasticsearch.search.aggregations.metrics.max.InternalMax
import org.elasticsearch.search.aggregations.metrics.min.InternalMin
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum
import org.elasticsearch.search.aggregations.metrics.valuecount.InternalValueCount
import org.scalatest.{FreeSpec, Matchers}
import com.sksamuel.elastic4s.testkit.ElasticSugar

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
      index into "aggregations/breakingbad" fields("name" -> "walter white", "job" -> "meth kingpin", "age" -> 50, "actor" -> "bryan"),
      index into "aggregations/breakingbad" fields("name" -> "hank schrader", "job" -> "dea agent", "age" -> 55, "actor" -> "dean"),
      index into "aggregations/breakingbad" fields("name" -> "jesse pinkman", "job" -> "meth sidekick", "age" -> 30),
      index into "aggregations/breakingbad" fields("name" -> "gus fring", "job" -> "meth kingpin", "age" -> 60),
      index into "aggregations/breakingbad" fields("name" -> "steven gomez", "job" -> "dea agent", "age" -> 50),
      index into "aggregations/breakingbad" fields("name" -> "saul goodman", "job" -> "lawyer", "age" -> 55),
      index into "aggregations/breakingbad" fields("name" -> "Huell Babineaux", "job" -> "heavy", "age" -> 43, "actor" -> "lavell"),
      index into "aggregations/breakingbad" fields("name" -> "mike ehrmantraut", "job" -> "heavy", "age" -> 45),
      index into "aggregations/breakingbad" fields("name" -> "lydia rodarte quayle", "job" -> "meth sidekick", "age" -> 40),
      index into "aggregations/breakingbad" fields("name" -> "todd alquist", "job" -> "meth sidekick", "age" -> 26)
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
      resp.totalHits shouldBe 10
      val agg = resp.aggregations.getAsMap.get("agg1").asInstanceOf[StringTerms]
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
      resp.totalHits shouldBe 3
      val aggs = resp.aggregations.getAsMap.get("agg1").asInstanceOf[StringTerms]
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
      resp.totalHits shouldBe 10
      val agg = resp.aggregations.getAsMap.get("agg1").asInstanceOf[InternalAvg]
      agg.getValue shouldBe 45.4
    }
    "should only include matching documents in the query" in {
      val resp = client.execute {
        // should match 3 documents
        search in "aggregations/breakingbad" query prefixQuery("name" -> "g") aggregations {
          aggregation avg "agg1" field "age"
        }
      }.await
      resp.totalHits shouldBe 3
      val agg = resp.aggregations.getAsMap.get("agg1").asInstanceOf[InternalAvg]
      agg.getValue shouldBe 55
    }
  }

  "cardinality aggregation" - {
    "should count distinct values" in {
      val resp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          aggregation cardinality "agg1" field "job"
        }
      }.await
      resp.totalHits shouldBe 10
      val aggs = resp.aggregations.getAsMap.get("agg1").asInstanceOf[InternalCardinality]
      aggs.getValue shouldBe 5
    }
  }

  "missing aggregation" - {
    "should return documents missing a value" in {
      val resp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          aggregation missing "agg1" field "actor"
        }
      }.await
      resp.totalHits shouldBe 10
      val aggs = resp.getAggregations.getAsMap.get("agg1").asInstanceOf[InternalMissing]
      aggs.getDocCount shouldBe 7
    }
  }

  "max aggregation" - {
    "should count max value for field" in {
      val resp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          aggregation max "agg1" field "age"
        }
      }.await
      resp.totalHits shouldBe 10
      val aggs = resp.aggregations.getAsMap.get("agg1").asInstanceOf[InternalMax]
      aggs.getValue shouldBe 60
    }
  }

  "min aggregation" - {
    "should count min value for field" in {
      val resp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          aggregation min "agg1" field "age"
        }
      }.await
      resp.totalHits shouldBe 10
      val aggs = resp.aggregations.getAsMap.get("agg1").asInstanceOf[InternalMin]
      aggs.getValue shouldBe 26
    }
  }

  "sum aggregation" - {
    "should sum values for field" in {
      val resp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          aggregation sum "agg1" field "age"
        }
      }.await
      resp.totalHits shouldBe 10
      val aggs = resp.aggregations.getAsMap.get("agg1").asInstanceOf[InternalSum]
      aggs.getValue shouldBe 454.0
    }
  }

  "value count aggregation" - {
    "should sum values for field" in {
      val resp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          aggregation count "agg1" field "age"
        }
      }.await
      resp.totalHits shouldBe 10
      val aggs = resp.aggregations.getAsMap.get("agg1").asInstanceOf[InternalValueCount]
      aggs.getValue shouldBe 10
    }
  }

  "range aggregation" - {
    "should range by field" in {
      val resp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          aggregation range "agg1" field "age" ranges(20.0 -> 30.0, 30.0 -> 40.0, 40.0 -> 50.0, 50.0 -> 60.0)
        }
      }.await
      resp.totalHits shouldBe 10
      import scala.collection.JavaConverters._
      val aggs = resp.aggregations.getAsMap.get("agg1").asInstanceOf[InternalRange[Bucket]]
      aggs.getBuckets.size shouldBe 4
      aggs.getBuckets.asScala.find(_.getKey == "20.0-30.0").get.getDocCount shouldBe 1
      aggs.getBuckets.asScala.find(_.getKey == "30.0-40.0").get.getDocCount shouldBe 1
      aggs.getBuckets.asScala.find(_.getKey == "40.0-50.0").get.getDocCount shouldBe 3
      aggs.getBuckets.asScala.find(_.getKey == "50.0-60.0").get.getDocCount shouldBe 4
    }
  }
}
