package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.searches.aggs.TopHitsAggregationDefinition
import com.sksamuel.elastic4s.searches.sort.{FieldSortDefinition, SortMode}
import org.scalatest.{FunSuite, Matchers}

class TopHitsAggregationBuilderTest extends FunSuite with Matchers {
  test("top hits aggregation should generate expected json") {
    val q = TopHitsAggregationDefinition("top_items")
      .size(5)
      .from(10)
      .version(true)
      .explain(false)
      .sortBy(List(FieldSortDefinition("price").sortMode(SortMode.Median)))
    TopHitsAggregationBuilder(q).string() shouldBe
      """{"top_hits":{"size":5,"from":10,"sort":[{"price":{"mode":"median","order":"asc"}}],"explain":false,"version":true}}"""
  }
}
