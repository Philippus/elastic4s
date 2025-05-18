package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.ElasticApi
import com.sksamuel.elastic4s.requests.searches.DateHistogramInterval
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class FilterAggregationDslTest extends AnyFlatSpec with Matchers with ElasticApi {

  "filter agg" should "support sub aggs" in {
    filterAgg(
      "filtered",
      boolQuery().must(
        rangeQuery("some_date_field").gte("now-1y")
      )
    ).addSubAggregation(
      dateHistogramAgg("per_month", "some_date_field")
        .calendarInterval(DateHistogramInterval.Month)
        .minDocCount(0L)
    )
  }
}
