package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.http.search.SearchBodyBuilderFn
import com.sksamuel.elastic4s.searches.{DateHistogramInterval, SearchDefinition}
import org.scalatest.{FunSuite, Matchers}

class SumBucketAggBuilderTest extends FunSuite with Matchers {

  import com.sksamuel.elastic4s.http.ElasticDsl._

  test("sum bucket agg should match the spec") {
    val search = SearchDefinition("myindex" / "mytype").aggs(
      dateHistogramAgg("sales_per_month", "date").interval(DateHistogramInterval.Month).addSubagg(
        sumAgg("sales", "price")
      ),
      sumBucketAggregation("sum_monthly_sales", "sales_per_month>sales")
    )
    SearchBodyBuilderFn(search).string() shouldBe
      """{"version":true,"aggs":{"sales_per_month":{"date_histogram":{"interval":"1M","field":"date"},"aggs":{"sales":{"sum":{"field":"price"}}}},"sum_monthly_sales":{"sum_bucket":{"buckets_path":"sales_per_month>sales"}}}}"""
  }
}
