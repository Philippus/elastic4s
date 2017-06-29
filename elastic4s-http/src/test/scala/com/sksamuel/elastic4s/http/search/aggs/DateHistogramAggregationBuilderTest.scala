package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.searches.aggs.DateHistogramAggregation
import org.elasticsearch.search.aggregations.bucket.histogram.{DateHistogramInterval, ExtendedBounds}
import org.scalatest.{FunSuite, Matchers}

class DateHistogramAggregationBuilderTest extends FunSuite with Matchers {

  test("date histogram aggregation with 'extendedBounds' should generate expected json") {
    val agg = DateHistogramAggregation("sales_over_time")
      .field("date")
      .interval(DateHistogramInterval.DAY)
      .extendedBounds(new ExtendedBounds("2015-01-01", "2017-01-01"))

    DateHistogramAggregationBuilder(agg).string() shouldBe
      """{"date_histogram":{"interval":"1d","field":"date","extended_bounds":{"min":"2015-01-01","max":"2017-01-01"}}}"""

  }

}
