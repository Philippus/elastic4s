package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.searches.aggs.{ChildrenAggregationDefinition, TopHitsAggregationDefinition}
import com.sksamuel.elastic4s.searches.queries.matches.MatchQueryDefinition
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.elastic4s.searches.sort.FieldSortDefinition
import org.scalatest.{FunSuite, Matchers}

class ChildrenAggregationBuilderTest extends FunSuite with Matchers {
  test("Children aggregation should generate expected json") {
    val agg = ChildrenAggregationDefinition("top_answers", "answer")
      .metadata(Map("app_version" -> "7.1"))
      .subAggregations(TopHitsAggregationDefinition("recent_answers").sortBy(FieldSortDefinition("date").desc()).size(4))
    ChildrenAggregationBuilder(agg).string() shouldBe
      """{"children":{"type":"answer"},"aggs":{"recent_answers":{"top_hits":{"size":4,"sort":[{"date":{"order":"desc"}}]}}},"meta":{"app_version":"7.1"}}"""
  }
}
