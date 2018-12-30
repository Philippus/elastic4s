package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.searches.aggs.pipeline.GapPolicy
import com.sksamuel.elastic4s.requests.searches.{DateHistogramInterval, SearchBodyBuilderFn, SearchRequest}
import org.scalatest.{FunSuite, Matchers}

import scala.concurrent.duration._

class DerivativeAggBuilderTest extends FunSuite with Matchers{

  import com.sksamuel.elastic4s.ElasticDsl._

  test("derivative agg should match the basic spec"){
    val search = SearchRequest("myIndex" / "myType").aggs(
      dateHistogramAgg("sales_per_month", "date").interval(DateHistogramInterval.Month)
        .addSubagg(
          sumAgg("sales", "price")
        ).addSubagg(
        derivativeAggregation("sales_deriv", "sales")
          .unit(1.day)
          .gapPolicy(GapPolicy.INSERT_ZEROS)
          .format("$").metadata(
          Map("color" -> "blue")
        )
      )
    )
    SearchBodyBuilderFn(search).string() shouldBe
      """{"aggs":{"sales_per_month":{"date_histogram":{"interval":"1M","field":"date"},"aggs":{"sales":{"sum":{"field":"price"}},"sales_deriv":{"derivative":{"buckets_path":"sales","unit":"86400s","gap_policy":"insert_zeros","format":"$"},"meta":{"color":"blue"}}}}}}"""
  }

}
