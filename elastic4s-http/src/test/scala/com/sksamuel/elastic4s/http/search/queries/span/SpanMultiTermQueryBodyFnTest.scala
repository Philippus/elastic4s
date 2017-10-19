package com.sksamuel.elastic4s.http.search.queries.span

import com.sksamuel.elastic4s.searches.queries.PrefixQueryDefinition
import com.sksamuel.elastic4s.searches.queries.span.SpanMultiTermQueryDefinition
import org.scalatest.FunSuite

import scala.util.parsing.json.JSON

class SpanMultiTermQueryBodyFnTest extends FunSuite {

  test("SpanMultiTermQueryBodyFn apply should return appropriate XContentBuilder") {
    val builder = SpanMultiTermQueryBodyFn.apply(SpanMultiTermQueryDefinition(
      PrefixQueryDefinition("user", "ki"),
      boost = Some(2.0),
      queryName = Some("rootName")
    ))

    val actual = JSON.parseRaw(builder.string())
    val expected = JSON.parseRaw(
      """
        |{
        |    "span_multi":{
        |        "match":{
        |            "prefix" : { "user" :  { "value" : "ki" } }
        |        },
        |        "boost":2.0,
        |        "_name":"rootName"
        |    }
        |}""".stripMargin)

    assert(actual === expected)
  }
}
