package com.sksamuel.elastic4s.http.search.queries.span

import com.sksamuel.elastic4s.searches.queries.span.{SpanFirstQueryDefinition, SpanTermQueryDefinition}
import org.scalatest.FunSuite

import scala.util.parsing.json.JSON

class SpanFirstQueryBodyFnTest extends FunSuite {

  test("SpanFirstQueryBodyFn apply should return appropriate XContentBuilder") {
    val builder = SpanFirstQueryBodyFn.apply(SpanFirstQueryDefinition(
      SpanTermQueryDefinition("field1", "value1"),
      end = 5,
      boost = Some(2.0),
      queryName = Some("rootName")
    ))

    val actual = JSON.parseRaw(builder.string())
    val expected = JSON.parseRaw(
      """
        |{
        |   "span_first":{
        |     "match":{
        |       "span_term":{"field1":"value1"}
        |     },
        |     "end":5,
        |     "boost":2.0,
        |     "_name":"rootName"
        |   }
        |}""".stripMargin)

    assert(actual === expected)
  }
}
