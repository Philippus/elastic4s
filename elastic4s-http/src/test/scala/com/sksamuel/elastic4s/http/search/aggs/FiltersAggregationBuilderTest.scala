package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.searches.aggs.{FiltersAggregationDefinition, TopHitsAggregationDefinition}
import com.sksamuel.elastic4s.searches.queries.matches.MatchQueryDefinition
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.elastic4s.searches.sort.FieldSortDefinition
import org.scalatest.{FunSuite, Matchers}

class FiltersAggregationBuilderTest extends FunSuite with Matchers {
  test("Filters aggregation should generate expected json") {
    val filters: Iterable[QueryDefinition] = Seq(MatchQueryDefinition("body", "error"), MatchQueryDefinition("body", "warning"))
    val agg = FiltersAggregationDefinition("filters", filters)
      .metadata(Map("app_version" -> "7.1"))
      .subAggregations(TopHitsAggregationDefinition("recent_logs").sortBy(FieldSortDefinition("date").desc()).size(4))
    FiltersAggregationBuilder(agg).string() shouldBe
      """{"filters":{"filters":[{"match":{"body":{"query":"error"}}},{"match":{"body":{"query":"warning"}}}]},"aggs":{"recent_logs":{"top_hits":{"size":4,"sort":[{"date":{"order":"desc"}}]}}},"meta":{"app_version":"7.1"}}"""
  }
}
