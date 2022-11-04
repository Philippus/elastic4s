package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.requests.searches.aggs.VariableWidthAggregation
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class VariableWidthAggregationBuilderTest extends AnyFunSuite with Matchers {

  test("VariableWidthAggregationBuilder should build simple json with field") {
    val search = SearchRequest("myindex").aggs(
      VariableWidthAggregation("score_histogram", "score")
    )
    SearchBodyBuilderFn(search).string shouldBe
      """{"aggs":{"score_histogram":{"variable_width_histogram":{"field":"score"}}}}"""
  }

  test("VariableWidthAggregationBuilder should respect all possible attributes") {
    val search = SearchRequest("myindex").aggs(
      VariableWidthAggregation("score_histogram", "score", shardSize = Some(500), initialBuffer = Some(100000))
    )
    SearchBodyBuilderFn(search).string shouldBe
      """{"aggs":{"score_histogram":{"variable_width_histogram":{"field":"score","shard_size":500,"initial_buffer":100000}}}}"""
  }
}
