package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.searches.aggs.TopHitsAggregation
import com.sksamuel.elastic4s.requests.searches.sort.{FieldSort, SortMode}
import org.scalatest.{FunSuite, Matchers}

class TopHitsAggregationBuilderTest extends FunSuite with Matchers {
  test("top hits aggregation should generate expected json") {
    val q = TopHitsAggregation("top_items")
      .size(5)
      .from(10)
      .version(true)
      .explain(false)
      .sortBy(List(FieldSort("price").sortMode(SortMode.Median)))
    TopHitsAggregationBuilder(q).string() shouldBe
      """{"top_hits":{"size":5,"from":10,"sort":[{"price":{"mode":"median","order":"asc"}}],"explain":false,"version":true}}"""
  }
}
