package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.searches.{DateHistogramInterval, SearchBodyBuilderFn, SearchRequest}
import org.scalatest.{FunSuite, Matchers}

class CumulativeSumAggBuilderTest extends FunSuite with Matchers{

  import com.sksamuel.elastic4s.ElasticDsl._

  test("cumulative sum agg should match the basic spec"){
    val search = SearchRequest("myIndex" / "myType").aggs(
      dateHistogramAgg("sales_per_month", "date")
        .interval(DateHistogramInterval.Month)
        .subaggs(
          Seq(
            sumAggregation("sales").field("price"),
            cumulativeSumAggregation("cumulative_sales", "sales")
              .format("$")
              .metadata(
              Map("color" -> "blue")
            )
          )
        )
    )
    SearchBodyBuilderFn(search).string() shouldBe
      """{"aggs":{"sales_per_month":{"date_histogram":{"interval":"1M","field":"date"},"aggs":{"sales":{"sum":{"field":"price"}},"cumulative_sales":{"cumulative_sum":{"buckets_path":"sales","format":"$"},"meta":{"color":"blue"}}}}}}"""
  }

}
