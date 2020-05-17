package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.requests.searches.aggs.builders
import com.sksamuel.elastic4s.requests.searches.aggs.builders.SigTermsAggregationBuilder
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SigTermsAggregationBuilderTest extends AnyFunSuite with Matchers {

  import com.sksamuel.elastic4s.ElasticDsl._

  test("sig terms aggregation with 'field' and 'background_filter' should generate expected json") {
    val agg = sigTermsAggregation("name")
      .field("field")
      .backgroundFilter(termQuery("text", "test"))

    builders.SigTermsAggregationBuilder(agg).string() shouldBe
      """{"significant_terms":{"field":"field","background_filter":{"term":{"text":{"value":"test"}}}}}"""
  }

  test("sig terms aggregation with heuristic 'percentage' should generate expected json") {
    val agg = sigTermsAggregation("name")
      .field("field")
      .significanceHeuristic("percentage")
    builders.SigTermsAggregationBuilder(agg).string() shouldBe
      """{"significant_terms":{"field":"field","percentage":{}}}"""
  }

  test("sig terms aggregation with heuristic 'mutual_information' with parameters should generate expected json") {
    val agg = sigTermsAggregation("name")
      .field("field")
      .significanceHeuristic(
        "mutual_information",
        Map(
          "include_negatives" -> true,
          "background_is_superset" -> false
        )
      )
    builders.SigTermsAggregationBuilder(agg).string() shouldBe
      """{"significant_terms":{"field":"field","mutual_information":{"include_negatives":true,"background_is_superset":false}}}"""
  }

}
