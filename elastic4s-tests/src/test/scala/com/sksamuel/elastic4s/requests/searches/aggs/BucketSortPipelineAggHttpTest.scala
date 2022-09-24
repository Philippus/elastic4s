package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.common.RefreshPolicy
import com.sksamuel.elastic4s.requests.searches.DateHistogramInterval
import com.sksamuel.elastic4s.requests.searches.aggs.responses.Aggregations
import com.sksamuel.elastic4s.requests.searches.aggs.responses.bucket.DateHistogram
import com.sksamuel.elastic4s.requests.searches.sort.{FieldSort, SortOrder}
import com.sksamuel.elastic4s.testkit.DockerTests
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Try

class BucketSortPipelineAggHttpTest extends AnyFreeSpec with DockerTests with Matchers {

  Try {
    client.execute {
      deleteIndex("bucketsortagg")
    }.await
  }

  client.execute {
    createIndex("bucketsortagg").mapping(
      properties(
        dateField("date"),
        doubleField("value").stored(true)
      )
    )
  }.await

  client.execute(
    bulk(
      indexInto("bucketsortagg") fields("date" -> "2017-01-01", "value" -> 1000.0),
      indexInto("bucketsortagg") fields("date" -> "2017-01-02", "value" -> 1000.0),
      indexInto("bucketsortagg") fields("date" -> "2017-02-01", "value" -> 2000.0),
      indexInto("bucketsortagg") fields("date" -> "2017-02-01", "value" -> 2000.0),
      indexInto("bucketsortagg") fields("date" -> "2017-03-01", "value" -> 3000.0),
      indexInto("bucketsortagg") fields("date" -> "2017-03-02", "value" -> 3000.0)
    ).refresh(RefreshPolicy.Immediate)
  ).await

  "bucket sort pipeline agg" - {
    "should return sorted buckets" in {

      val resp = client.execute {
        search("bucketsortagg").matchAllQuery().aggs(
          dateHistogramAgg("sales_per_month", "date")
            .calendarInterval(DateHistogramInterval.Month)
            .subaggs(
              sumAgg("sales", "value"),
              bucketSortAggregation("sales_bucket_sort",
                Seq(FieldSort("sales").order(SortOrder.DESC)))
            )
        )
      }.await.result

      resp.totalHits shouldBe 6

      val buckets = resp.aggs.result[DateHistogram]("sales_per_month").buckets

      buckets.size shouldBe 3
      Aggregations(buckets.head.data).sum("sales").value shouldBe 6000.0
      Aggregations(buckets(1).data).sum("sales").value shouldBe 4000.0
      Aggregations(buckets(2).data).sum("sales").value shouldBe 2000.0
    }
  }

  "should limit sorted buckets" in {

    val resp = client.execute {
      search("bucketsortagg").matchAllQuery().aggs(
        dateHistogramAgg("sales_per_month", "date")
          .calendarInterval(DateHistogramInterval.Month)
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

    val buckets = resp.aggs.result[DateHistogram]("sales_per_month").buckets

    buckets.size shouldBe 1
    Aggregations(buckets.head.data).sum("sales").value shouldBe 4000.0
  }
}
