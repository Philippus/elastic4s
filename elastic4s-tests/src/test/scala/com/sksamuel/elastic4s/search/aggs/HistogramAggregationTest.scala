package com.sksamuel.elastic4s.search.aggs

import com.sksamuel.elastic4s.searches.aggs.HistogramOrder

import scala.collection.JavaConverters._

class HistogramAggregationTest extends AbstractAggregationTest {

  "histogram aggregation" - {
    "should create histogram by field" in {
      val resp = client.execute {
        search("aggregations/breakingbad") aggregations {
          histogramAggregation("h") field "age" interval 10
        }
      }.await
      resp.totalHits shouldBe 10

      val buckets = resp.aggregations.histogramResult("h").getBuckets.asScala
      buckets.size shouldBe 5
      buckets.find(_.getKey == 20).get.getDocCount shouldBe 1
      buckets.find(_.getKey == 30).get.getDocCount shouldBe 1
      buckets.find(_.getKey == 40).get.getDocCount shouldBe 3
      buckets.find(_.getKey == 50).get.getDocCount shouldBe 4
      buckets.find(_.getKey == 60).get.getDocCount shouldBe 1
    }
    "should use offset" in {
      val resp = client.execute {
        search("aggregations/breakingbad") aggregations {
          histogramAggregation("h") field "age" interval 10 offset 5
        }
      }.await
      resp.totalHits shouldBe 10

      val buckets = resp.aggregations.histogramResult("h").getBuckets.asScala
      buckets.size shouldBe 4
      buckets.find(_.getKey == 25).get.getDocCount shouldBe 2
      buckets.find(_.getKey == 35).get.getDocCount shouldBe 2
      buckets.find(_.getKey == 45).get.getDocCount shouldBe 3
      buckets.find(_.getKey == 55).get.getDocCount shouldBe 3
    }

    "should respect min_doc_count" in {
      val resp = client.execute {
        search("aggregations/breakingbad") aggregations {
          histogramAggregation("agg1") field "age" interval 10 minDocCount 2
        }
      }.await
      resp.totalHits shouldBe 10
      val buckets = resp.aggregations.histogramResult("agg1").getBuckets.asScala
      buckets.size shouldBe 2
      buckets.find(_.getKey == 40).get.getDocCount shouldBe 3
      buckets.find(_.getKey == 50).get.getDocCount shouldBe 4
    }

    "should respect ordering" in {
      val resp = client.execute {
        search("aggregations/breakingbad") aggregations {
          histogramAggregation("agg1") field "age" interval 10 order HistogramOrder.COUNT_ASC
        }
      }.await
      resp.totalHits shouldBe 10
      val buckets = resp.aggregations.histogramResult("agg1").getBuckets.asScala
      buckets.size shouldBe 5
      // should be lowest qty buckets first
      buckets.map(_.getKeyAsString) shouldBe Seq("20.0", "30.0", "60.0", "40.0", "50.0")
    }
  }
}
