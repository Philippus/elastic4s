package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.requests.searches.aggs.{SigTextAggregation, builders}
import com.sksamuel.elastic4s.requests.searches.aggs.builders.SigTextAggregationBuilder
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SigTextAggregationBuilderTest extends AnyFunSuite with Matchers {
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
    builders.SigTextAggregationBuilder(agg).string() shouldBe
      """{"significant_text":{"field":"field","mutual_information":{"include_negatives":true,"background_is_superset":false}}}"""
  }
}
