package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.searches.aggs.HistogramAggregation
import org.scalatest.{FunSuite, Matchers}

class HistogramAggregationBuilderTest extends FunSuite with Matchers {

  test("histogram aggregation should generate expected json") {
    val agg = HistogramAggregation("prices")
      .field("price")
      .missing(Int.box(0))
      .minDocCount(1)
      .interval(50)
      .keyed(true)
      .extendedBounds(0, 500)

    HistogramAggregationBuilder(agg).string() shouldBe
      """
      | {
      |   "histogram": {
      |     "field": "pr ice",
      |     "missing": 0,
      |     "min_doc_count": 1,
      |     "interval": 50.0,
      |     "keyed": true,
      |     "extended_bounds": {
      |       "min": 0.0,
      |       "max": 500.0
      |     }
      |   }
      | }
      |""".stripMargin.replaceAll("\\s", "")

  }

}
