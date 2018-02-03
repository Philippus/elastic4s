package com.sksamuel.elastic4s.search.aggs

import com.sksamuel.elastic4s.http.search.Aggregations
import com.sksamuel.elastic4s.searches.DateHistogramInterval
import com.sksamuel.elastic4s.searches.sort.{FieldSort, SortOrder}
import com.sksamuel.elastic4s.RefreshPolicy
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.{FreeSpec, Matchers}

import scala.util.Try

class BucketSortPipelineAggHttpTest extends FreeSpec with DockerTests with Matchers {

  Try {
    http.execute {
      deleteIndex("bucketsortagg")
    }.await
  }

  http.execute {
    createIndex("bucketsortagg") mappings {
      mapping("sales") fields(
        dateField("date"),
        doubleField("value").stored(true)
      )
    }
  }.await

  http.execute(
    bulk(
      indexInto("bucketsortagg/sales") fields("date" -> "2017-01-01", "value" -> 1000.0),
      indexInto("bucketsortagg/sales") fields("date" -> "2017-01-02", "value" -> 1000.0),
      indexInto("bucketsortagg/sales") fields("date" -> "2017-02-01", "value" -> 2000.0),
      indexInto("bucketsortagg/sales") fields("date" -> "2017-02-01", "value" -> 2000.0),
      indexInto("bucketsortagg/sales") fields("date" -> "2017-03-01", "value" -> 3000.0),
      indexInto("bucketsortagg/sales") fields("date" -> "2017-03-02", "value" -> 3000.0)
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "bucket sort pipeline agg" - {
    "should return sorted buckets" in {

      val resp = http.execute {
        search("bucketsortagg").matchAllQuery().aggs(
          dateHistogramAgg("sales_per_month", "date")
            .interval(DateHistogramInterval.Month)
            .subaggs (
              sumAgg("sales", "value"),
              bucketSortAggregation("sales_bucket_sort",
                Seq(FieldSort("sales").order(SortOrder.DESC)))
            )
        )
      }.await.result

      resp.totalHits shouldBe 6

      val buckets = resp.aggs.dateHistogram("sales_per_month").buckets

      buckets.size shouldBe 3
      Aggregations(buckets(0).data).sum("sales").value shouldBe 6000.0
      Aggregations(buckets(1).data).sum("sales").value shouldBe 4000.0
      Aggregations(buckets(2).data).sum("sales").value shouldBe 2000.0
    }
  }

  "should limit sorted buckets" in {

    val resp = http.execute {
      search("bucketsortagg").matchAllQuery().aggs(
        dateHistogramAgg("sales_per_month", "date")
          .interval(DateHistogramInterval.Month)
          .subaggs (
            sumAgg("sales", "value"),
            bucketSortAggregation("sales_bucket_sort",
              Seq(FieldSort("sales").order(SortOrder.DESC)))
              .size(1)
              .from(1)
          )
      )
    }.await.result

    resp.totalHits shouldBe 6

    val buckets = resp.aggs.dateHistogram("sales_per_month").buckets

    buckets.size shouldBe 1
    Aggregations(buckets(0).data).sum("sales").value shouldBe 4000.0
  }
}
