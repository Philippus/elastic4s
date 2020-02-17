package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.searches.aggs.SigTextAggregation
import org.scalatest.{FunSuite, Matchers}

class SigTextAggregationTest extends FunSuite with Matchers {

  test("sig terms aggregation with heuristic 'percentage' should generate expected json") {
    val agg = SigTextAggregation("name")
      .field("field")
      .significanceHeuristic("percentage")
    SigTextAggregationBuilder(agg).string() shouldBe
      """{"significant_text":{"field":"field","percentage":{}}}"""
  }

  test("sig terms aggregation with heuristic 'mutual_information' with parameters should generate expected json") {
    val agg = SigTextAggregation("name")
      .field("field")
      .significanceHeuristic(
        "mutual_information",
        Map(
          "include_negatives" -> true,
          "background_is_superset" -> false
        )
      )
    SigTextAggregationBuilder(agg).string() shouldBe
      """{"significant_text":{"field":"field","mutual_information":{"include_negatives":true,"background_is_superset":false}}}"""
  }
}
