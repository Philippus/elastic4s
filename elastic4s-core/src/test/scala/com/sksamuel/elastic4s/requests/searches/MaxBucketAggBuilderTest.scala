package com.sksamuel.elastic4s.requests.searches

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class MaxBucketAggBuilderTest extends AnyFunSuite with Matchers {

  import com.sksamuel.elastic4s.ElasticDsl._

  test("max bucket agg should match the spec") {
    val search = SearchRequest("myindex").aggs(
      dateHistogramAgg("sales_per_month", "date").calendarInterval(DateHistogramInterval.Month).addSubagg(
        sumAgg("sales", "price")
      ),
      maxBucketAgg("max_monthly_sales", "sales_per_month>sales")
    )
    SearchBodyBuilderFn(search).string() shouldBe
      """{"aggs":{"sales_per_month":{"date_histogram":{"calendar_interval":"1M","field":"date"},"aggs":{"sales":{"sum":{"field":"price"}}}},"max_monthly_sales":{"max_bucket":{"buckets_path":"sales_per_month>sales"}}}}"""
  }
}
