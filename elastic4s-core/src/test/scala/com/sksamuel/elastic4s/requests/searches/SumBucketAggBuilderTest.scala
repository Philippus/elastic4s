package com.sksamuel.elastic4s.requests.searches

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SumBucketAggBuilderTest extends AnyFunSuite with Matchers {

  import com.sksamuel.elastic4s.ElasticDsl._

  test("sum bucket agg should match the spec") {
    val search = SearchRequest("myindex").aggs(
      dateHistogramAgg("sales_per_month", "date").calendarInterval(DateHistogramInterval.Month).addSubagg(
        sumAgg("sales", "price")
      ),
      sumBucketAggregation("sum_monthly_sales", "sales_per_month>sales")
    )
    SearchBodyBuilderFn(search).string() shouldBe
      """{"aggs":{"sales_per_month":{"date_histogram":{"calendar_interval":"1M","field":"date"},"aggs":{"sales":{"sum":{"field":"price"}}}},"sum_monthly_sales":{"sum_bucket":{"buckets_path":"sales_per_month>sales"}}}}"""
  }
}
