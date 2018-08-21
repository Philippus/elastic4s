package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.searches.aggs.{KeyedFiltersAggregationDefinition, TopHitsAggregationDefinition}
import com.sksamuel.elastic4s.searches.queries.matches.MatchQueryDefinition
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.elastic4s.searches.sort.FieldSortDefinition
import org.scalatest.{FunSuite, Matchers}

class KeyedFiltersAggregationBuilderTest extends FunSuite with Matchers {
  val filters: Iterable[(String, QueryDefinition)] = Seq(
    ("error_logs", MatchQueryDefinition("body", "error")),
    ("warning_logs", MatchQueryDefinition("body", "warning"))
  )

  test("KeyedFilters aggregation with missing otherBucket should generate expected json") {
    val agg = KeyedFiltersAggregationDefinition("filters", filters)
      .metadata(Map("app_version" -> "7.1"))
      .subAggregations(TopHitsAggregationDefinition("recent_logs").sortBy(FieldSortDefinition("date").desc()).size(4))
    KeyedFiltersAggregationBuilder(agg).string() shouldBe
      """{"filters":{"filters":{"error_logs":{"match":{"body":{"query":"error"}}},"warning_logs":{"match":{"body":{"query":"warning"}}}}},"aggs":{"recent_logs":{"top_hits":{"size":4,"sort":[{"date":{"order":"desc"}}]}}},"meta":{"app_version":"7.1"}}"""
  }

  test("KeyedFilters aggregation with default otherBucket should generate expected json") {
    val agg = KeyedFiltersAggregationDefinition("filters", filters, Some(true))
    KeyedFiltersAggregationBuilder(agg).string() shouldBe
      """{"filters":{"other_bucket":true,"filters":{"error_logs":{"match":{"body":{"query":"error"}}},"warning_logs":{"match":{"body":{"query":"warning"}}}}}}"""
  }

  test("KeyedFilters aggregation with explicitely excluded otherBucket should generate expected json") {
    val agg = KeyedFiltersAggregationDefinition("filters", filters, Some(false))
    KeyedFiltersAggregationBuilder(agg).string() shouldBe
      """{"filters":{"other_bucket":false,"filters":{"error_logs":{"match":{"body":{"query":"error"}}},"warning_logs":{"match":{"body":{"query":"warning"}}}}}}"""
  }

  test("KeyedFilters aggregation with specified key for otherBucket should generate expected json") {
    val agg = KeyedFiltersAggregationDefinition("filters", filters, Some(true), Some("success_logs"))
    KeyedFiltersAggregationBuilder(agg).string() shouldBe
      """{"filters":{"other_bucket":true,"other_bucket_key":"success_logs","filters":{"error_logs":{"match":{"body":{"query":"error"}}},"warning_logs":{"match":{"body":{"query":"warning"}}}}}}"""
  }
}
