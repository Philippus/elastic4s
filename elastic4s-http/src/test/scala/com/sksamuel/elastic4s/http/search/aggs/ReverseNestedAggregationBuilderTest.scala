package com.sksamuel.elastic4s.http.search.aggs


import com.sksamuel.elastic4s.searches.SearchDefinition
import com.sksamuel.elastic4s.searches.aggs.ReverseNestedAggregationDefinition
import org.scalatest.{FunSuite, Matchers}

class ReverseNestedAggregationBuilderTest extends FunSuite with Matchers {

  import com.sksamuel.elastic4s.http.ElasticDsl._

  test("reverse aggregation should match the spec") {
    val search = ReverseNestedAggregationDefinition("reverse_nested_agg_name").subaggs(
      sumAgg("sales", "price")
    )
    ReverseNestedAggregationBuilder(search).string() shouldBe
      """{"reverse_nested":{},"aggs":{"sales":{"sum":{"field":"price"}}}}"""
  }

  test("reverse aggregation with path specified should match the spec") {
    val search = ReverseNestedAggregationDefinition("reverse_nested_agg_name", Some("nested_path.sub_nedsted_path")).subaggs(
      sumAgg("sales", "price")
    )
    ReverseNestedAggregationBuilder(search).string() shouldBe
      """{"reverse_nested":{"path":"nested_path.sub_nedsted_path"},"aggs":{"sales":{"sum":{"field":"price"}}}}"""
  }
}
