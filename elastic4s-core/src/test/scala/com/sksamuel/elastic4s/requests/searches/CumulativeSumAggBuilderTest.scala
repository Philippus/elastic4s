package com.sksamuel.elastic4s.requests.searches

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class CumulativeSumAggBuilderTest extends AnyFunSuite with Matchers {

  import com.sksamuel.elastic4s.ElasticDsl._

  test("cumulative sum agg should match the basic spec") {
    val search = SearchRequest("myIndex").aggs(
      dateHistogramAgg("sales_per_month", "date")
        .fixedInterval(DateHistogramInterval.Month)
        .subaggs(
          Seq(
            sumAgg("sales", "price"),
            cumulativeSumAggregation("cumulative_sales", "sales")
              .format("$")
              .metadata(
                Map("color" -> "blue")
              )
          )
        )
    )
    SearchBodyBuilderFn(search).string shouldBe
      """{"aggs":{"sales_per_month":{"date_histogram":{"fixed_interval":"1M","field":"date"},"aggs":{"sales":{"sum":{"field":"price"}},"cumulative_sales":{"cumulative_sum":{"buckets_path":"sales","format":"$"},"meta":{"color":"blue"}}}}}}"""
  }

}
