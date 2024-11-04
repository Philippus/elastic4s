package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.requests.searches.aggs.TopHitsAggregation
import com.sksamuel.elastic4s.requests.searches.aggs.builders.TopHitsAggregationBuilder
import com.sksamuel.elastic4s.requests.searches.sort.{FieldSort, SortMode}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class TopHitsAggregationBuilderTest extends AnyFunSuite with Matchers {
  test("top hits aggregation builder should generate expected json") {
    val q = TopHitsAggregation("top_items")
      .size(5)
      .from(10)
      .version(true)
      .explain(false)
      .docValueFields(List("name"))
      .storedFields(List("currency"))
      .sortBy(List(FieldSort("price").sortMode(SortMode.Median)))
    TopHitsAggregationBuilder(q).string shouldBe
      """{"top_hits":{"size":5,"from":10,"sort":[{"price":{"mode":"median","order":"asc"}}],"explain":false,"stored_fields":["currency"],"docvalue_fields":["name"],"version":true}}"""
  }

  test("top hits aggregation builder should support highlighting") {
    val q = TopHitsAggregation("top_items")
      .highlighting(HighlightField("name"))
    TopHitsAggregationBuilder(q).string shouldBe
      """{"top_hits":{"highlight":{"fields":{"name":{}}}}}"""
  }
}
