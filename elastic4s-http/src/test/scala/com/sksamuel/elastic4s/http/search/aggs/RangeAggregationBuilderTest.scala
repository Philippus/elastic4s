package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.elastic4s.searches.aggs.RangeAggregationDefinition
import org.scalatest.{FunSuite, Matchers}

class RangeAggregationBuilderTest extends FunSuite with Matchers {

  test("range aggregation with 'field' and 'ranges' should generate expected json") {
    val agg = RangeAggregationDefinition("price_ranges")
      .field("price")
      .range(0, 50)
      .range(50, 100)
      .range(100, 1000)

    RangeAggregationBuilder(agg).string() shouldBe
      """{"range":{"field":"price","ranges":[{"from":0.0,"to":50.0},{"from":50.0,"to":100.0},{"from":100.0,"to":1000.0}]}}"""
  }

  test("range aggregation with 'unboundedFrom' and 'unboundedTo' should generate expected json") {
    val agg = RangeAggregationDefinition("price_ranges")
      .field("price")
      .unboundedTo(50)
      .range(50, 100)
      .unboundedFrom(100)

    RangeAggregationBuilder(agg).string() shouldBe
      """{"range":{"field":"price","ranges":[{"to":50.0},{"from":50.0,"to":100.0},{"from":100.0}]}}"""
  }

  test("range aggregation with 'named ranges' should generate expected json") {
    val agg = RangeAggregationDefinition("price_ranges")
      .field("price")
      .unboundedTo("cheap", 50)
      .range("average", 50, 100)
      .unboundedFrom("expensive", 100)

    RangeAggregationBuilder(agg).string() shouldBe
      """{"range":{"field":"price","ranges":[{"key":"cheap","to":50.0},{"key":"average","from":50.0,"to":100.0},{"key":"expensive","from":100.0}]}}"""
  }

  test("range aggregation with a script parameter should generate expected json") {
    val agg = RangeAggregationDefinition("price_ranges")
      .field("price")
      .script(ScriptDefinition("doc['price'].value").lang("painless"))
      .range(0, 50)
      .range(50, 1000)

    RangeAggregationBuilder(agg).string() shouldBe
      """{"range":{"field":"price","script":{"lang":"painless","inline":"doc['price'].value"},"ranges":[{"from":0.0,"to":50.0},{"from":50.0,"to":1000.0}]}}"""
  }

  test("range aggregation with a keyed parameter setted to true should generate expected json") {
    val agg = RangeAggregationDefinition("price_ranges")
      .field("price")
      .range(0, 50)
      .range(50, 1000)
      .keyed(true)

    RangeAggregationBuilder(agg).string() shouldBe
      """{"range":{"field":"price","keyed":true,"ranges":[{"from":0.0,"to":50.0},{"from":50.0,"to":1000.0}]}}"""
  }

  test("range aggregation with a missing parameter setted should generate expected json") {
    val agg = RangeAggregationDefinition("price_ranges")
      .field("price")
      .missing(Int.box(0))
      .range(0, 50)
      .range(50, 1000)

    RangeAggregationBuilder(agg).string() shouldBe
      """{"range":{"field":"price","missing":0,"ranges":[{"from":0.0,"to":50.0},{"from":50.0,"to":1000.0}]}}"""
  }

}
