package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.searches.aggs.DateRangeAggregation
import org.joda.time.DateTimeZone
import org.scalatest.{FunSuite, Matchers}

class DateRangeAggregationBuilderTest extends FunSuite with Matchers {

  test("date range should generate expected json") {
    val agg = DateRangeAggregation("series")
      .field("createdAt")
      .timeZone(DateTimeZone.forID("EST"))
      .range( "Today", "now/d","now+1d/d")
      .range( "Yesterday", "now/d-1d","now/d")

    DateRangeAggregationBuilder(agg).string() shouldBe
      """{"date_range":{"field":"createdAt","time_zone":"EST","ranges":[{"key":"Today","from":"now/d","to":"now+1d/d"},{"key":"Yesterday","from":"now/d-1d","to":"now/d"}]}}"""
  }

}
