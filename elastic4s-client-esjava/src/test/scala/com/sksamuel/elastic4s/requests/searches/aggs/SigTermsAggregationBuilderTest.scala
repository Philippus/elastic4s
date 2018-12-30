package com.sksamuel.elastic4s.requests.searches.aggs

import org.scalatest.{FunSuite, Matchers}

class SigTermsAggregationBuilderTest extends FunSuite with Matchers{

  import com.sksamuel.elastic4s.ElasticDsl._

  test("sig terms aggregation with 'field' and 'background_filter' should generate expected json") {
    val agg = sigTermsAggregation("name")
      .field("field")
      .backgroundFilter(termQuery("text", "test"))

    SigTermsAggregationBuilder(agg).string() shouldBe
      """{"significant_terms":{"field":"field","background_filter":{"term":{"text":{"value":"test"}}}}}"""
  }


}
