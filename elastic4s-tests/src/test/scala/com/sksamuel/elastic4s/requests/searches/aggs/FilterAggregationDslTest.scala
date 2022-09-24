package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.ElasticApi
import com.sksamuel.elastic4s.requests.searches.DateHistogramInterval
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class FilterAggregationDslTest extends AnyFlatSpec with Matchers with ElasticApi {

  "filter agg" should "support sub aggs" in {
    filterAggregation("filtered").query(
      boolQuery().must(
        rangeQuery("some_date_field").gte("now-1y")
      )
    ).addSubAggregation(
      dateHistogramAggregation("per_month")
        .field("some_date_field")
        .calendarInterval(DateHistogramInterval.Month)
        .minDocCount(0L)
    )
  }
}
