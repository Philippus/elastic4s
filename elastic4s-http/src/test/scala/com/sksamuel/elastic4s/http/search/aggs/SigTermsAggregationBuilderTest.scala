package com.sksamuel.elastic4s.http.search.aggs

import org.scalatest.{FunSuite, Matchers}

class SigTermsAggregationBuilderTest extends FunSuite with Matchers {

  import com.sksamuel.elastic4s.http.ElasticDsl._

  test("sig terms aggregation with 'field' and 'background_filter' should generate expected json") {
    val agg = sigTermsAggregation("name")
      .field("field")
      .backgroundFilter(termQuery("text", "test"))

    SigTermsAggregationBuilder(agg).string() shouldBe
      """{"significant_terms":{"field":"field","background_filter":{"term":{"text":{"value":"test"}}}}}"""
  }

  test("sig terms aggregation with heuristic 'percentage' should generate expected json") {
    val agg = sigTermsAggregation("name")
      .field("field")
      .significanceHeuristic("percentage")
    SigTermsAggregationBuilder(agg).string() shouldBe
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
    SigTermsAggregationBuilder(agg).string() shouldBe
      """{"significant_terms":{"field":"field","mutual_information":{"include_negatives":true,"background_is_superset":false}}}"""
  }


}
