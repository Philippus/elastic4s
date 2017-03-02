package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.ElasticApi
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval
import org.scalatest.{FlatSpec, Matchers}

class FilterAggregationDslTest extends FlatSpec with Matchers with ElasticApi {

  "filter agg" should "support sub aggs" in {
    filterAggregation("filtered").query(
      filter(
        rangeQuery("some_date_field")
          .gte("now-1y")
          .to("now")
      )
    ).subAggregation(
      dateHistogramAggregation("per_month")
        .field("some_date_field")
        .interval(DateHistogramInterval.MONTH)
        .minDocCount(0L)
    )
  }
}
