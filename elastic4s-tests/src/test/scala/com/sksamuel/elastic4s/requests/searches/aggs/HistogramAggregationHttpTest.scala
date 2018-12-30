package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.searches.HistogramBucket
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class HistogramAggregationHttpTest extends FreeSpec with Matchers with DockerTests {

  Try {
    client.execute {
      deleteIndex("histogram")
    }.await
  }

  client.execute {
    createIndex("histogram") mappings {
      mapping("breakingbad") fields(
        keywordField("job"),
        keywordField("actor")
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("histogram/breakingbad") fields("name" -> "walter white", "job" -> "meth kingpin", "age" -> 50, "actor" -> "bryan"),
      indexInto("histogram/breakingbad") fields("name" -> "hank schrader", "job" -> "dea agent", "age" -> 55, "actor" -> "dean"),
      indexInto("histogram/breakingbad") fields("name" -> "jesse pinkman", "job" -> "meth sidekick", "age" -> 30),
      indexInto("histogram/breakingbad") fields("name" -> "gus fring", "job" -> "meth kingpin", "age" -> 60),
      indexInto("histogram/breakingbad") fields("name" -> "steven gomez", "job" -> "dea agent", "age" -> 50),
      indexInto("histogram/breakingbad") fields("name" -> "saul goodman", "job" -> "lawyer", "age" -> 55),
      indexInto("histogram/breakingbad") fields("name" -> "Huell Babineaux", "job" -> "heavy", "age" -> 43, "actor" -> "lavell"),
      indexInto("histogram/breakingbad") fields("name" -> "mike ehrmantraut", "job" -> "heavy", "age" -> 45),
      indexInto("histogram/breakingbad") fields("name" -> "lydia rodarte quayle", "job" -> "meth sidekick", "age" -> 40),
      indexInto("histogram/breakingbad") fields("name" -> "todd alquist", "job" -> "meth sidekick", "age" -> 26)
    ).refreshImmediately
  ).await

  "histogram aggregation" - {
    "should create histogram by field" in {
      val resp = client.execute {
        search("histogram") aggregations {
          histogramAggregation("h") field "age" interval 10
        }
      }.await.result

      resp.totalHits shouldBe 10

      val agg = resp.aggs.histogram("h")
      agg.buckets.map(_.copy(data = Map.empty)) shouldBe Seq(
        HistogramBucket("20.0", 1, Map.empty),
        HistogramBucket("30.0", 1, Map.empty),
        HistogramBucket("40.0", 3, Map.empty),
        HistogramBucket("50.0", 4, Map.empty),
        HistogramBucket("60.0", 1, Map.empty)
      )
    }
    "should respect min_doc_count" in {

      val resp = client.execute {
        search("histogram") aggregations {
          histogramAggregation("agg1") field "age" interval 10 minDocCount 2
        }
      }.await.result

      resp.totalHits shouldBe 10

      val agg = resp.aggs.histogram("agg1")
      agg.buckets.map(_.copy(data = Map.empty)) shouldBe Seq(
        HistogramBucket("40.0", 3, Map.empty),
        HistogramBucket("50.0", 4, Map.empty)
      )
    }

    "should respect ordering" in {

      val resp = client.execute {
        search("histogram") aggregations {
          histogramAggregation("agg1") field "age" interval 10 order HistogramOrder.COUNT_ASC
        }
      }.await.result

      resp.totalHits shouldBe 10

      val agg = resp.aggs.histogram("agg1")
      agg.buckets.map(_.copy(data = Map.empty)) shouldBe Seq(
        HistogramBucket("20.0", 1, Map.empty),
        HistogramBucket("30.0", 1, Map.empty),
        HistogramBucket("60.0", 1, Map.empty),
        HistogramBucket("40.0", 3, Map.empty),
        HistogramBucket("50.0", 4, Map.empty)
      )
    }
  }
}
