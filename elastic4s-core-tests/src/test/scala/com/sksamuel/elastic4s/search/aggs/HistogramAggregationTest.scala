package com.sksamuel.elastic4s.search.aggs

import com.sksamuel.elastic4s.testkit.ElasticSugar
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram
import org.scalatest.{FreeSpec, Matchers}
import scala.collection.JavaConverters._

class HistogramAggregationTest extends FreeSpec with Matchers with ElasticSugar {

  client.execute {
    createIndex("aggregations") mappings {
      mapping("breakingbad") fields(
        keywordField("job"),
        keywordField("actor")
      )
    }
  }.await

  client.execute(
    bulk(
      indexInto("aggregations/breakingbad") fields("name" -> "walter white", "job" -> "meth kingpin", "age" -> 50, "actor" -> "bryan"),
      indexInto("aggregations/breakingbad") fields("name" -> "hank schrader", "job" -> "dea agent", "age" -> 55, "actor" -> "dean"),
      indexInto("aggregations/breakingbad") fields("name" -> "jesse pinkman", "job" -> "meth sidekick", "age" -> 30),
      indexInto("aggregations/breakingbad") fields("name" -> "gus fring", "job" -> "meth kingpin", "age" -> 60),
      indexInto("aggregations/breakingbad") fields("name" -> "steven gomez", "job" -> "dea agent", "age" -> 50),
      indexInto("aggregations/breakingbad") fields("name" -> "saul goodman", "job" -> "lawyer", "age" -> 55),
      indexInto("aggregations/breakingbad") fields("name" -> "Huell Babineaux", "job" -> "heavy", "age" -> 43, "actor" -> "lavell"),
      indexInto("aggregations/breakingbad") fields("name" -> "mike ehrmantraut", "job" -> "heavy", "age" -> 45),
      indexInto("aggregations/breakingbad") fields("name" -> "lydia rodarte quayle", "job" -> "meth sidekick", "age" -> 40),
      indexInto("aggregations/breakingbad") fields("name" -> "todd alquist", "job" -> "meth sidekick", "age" -> 26)
    )
  ).await

  refresh("aggregations")
  blockUntilCount(10, "aggregations")

  "histogram aggregation" - {
    "should create histogram by field" in {
      val resp = client.execute {
        search in "aggregations/breakingbad" aggregations {
          aggregation histogram "h" field "age" interval 10
        }
      }.await
      resp.totalHits shouldBe 10

      val buckets = resp.aggregations.get[Histogram]("h").getBuckets.asScala
      buckets.size shouldBe 5
      buckets.find(_.getKey == 20).get.getDocCount shouldBe 1
      buckets.find(_.getKey == 30).get.getDocCount shouldBe 1
      buckets.find(_.getKey == 40).get.getDocCount shouldBe 3
      buckets.find(_.getKey == 50).get.getDocCount shouldBe 4
      buckets.find(_.getKey == 60).get.getDocCount shouldBe 1
    }
  }
}
