package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.http.search.SearchBodyBuilderFn
import com.sksamuel.elastic4s.searches.{DateHistogramInterval, SearchRequest}
import org.scalatest.{FunSuite, Matchers}

import scala.concurrent.duration._

class MaxBucketAggBuilderTest extends FunSuite with Matchers {

  import com.sksamuel.elastic4s.http.ElasticDsl._

  test("max bucket agg should match the spec") {
    val search = SearchRequest("myindex" / "mytype").aggs(
      dateHistogramAgg("sales_per_month", "date").interval(DateHistogramInterval.Month).addSubagg(
        sumAgg("sales", "price")
      ),
      maxBucketAgg("max_monthly_sales", "sales_per_month>sales")
    )
    SearchBodyBuilderFn(search).string() shouldBe
      """{"aggs":{"sales_per_month":{"date_histogram":{"interval":"1M","field":"date"},"aggs":{"sales":{"sum":{"field":"price"}}}},"max_monthly_sales":{"max_bucket":{"buckets_path":"sales_per_month>sales"}}}}"""
  }
}
