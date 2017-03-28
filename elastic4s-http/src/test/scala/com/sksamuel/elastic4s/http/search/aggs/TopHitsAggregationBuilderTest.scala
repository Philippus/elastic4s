package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.searches.aggs.TopHitsAggregationDefinition
import com.sksamuel.elastic4s.searches.sort.FieldSortDefinition
import org.scalatest.{FunSuite, Matchers}

class TopHitsAggregationBuilderTest extends FunSuite with Matchers {
  test("top hits aggregation should generate expected json") {
    val q = TopHitsAggregationDefinition("top_items")
      .size(5)
      .version(true)
      .explain(false)
      .sortBy(List(FieldSortDefinition("price")))
    TopHitsAggregationBuilder(q).string() shouldBe
      """{"top_hits":{"size":5,"sort":[{"price":{"order":"asc"}}],"explain":false,"version":true},"aggs":{}}"""
  }
}
