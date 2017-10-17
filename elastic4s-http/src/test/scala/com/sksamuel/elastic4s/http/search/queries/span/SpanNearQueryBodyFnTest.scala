package com.sksamuel.elastic4s.http.search.queries.span

import com.sksamuel.elastic4s.searches.queries.span.{SpanNearQueryDefinition, SpanTermQueryDefinition}
import org.scalatest.FunSuite

import scala.util.parsing.json.JSON

class SpanNearQueryBodyFnTest extends FunSuite {

  test("SpanNearQueryBodyFn apply should return appropriate XContentBuilder") {
    val builder = SpanNearQueryBodyFn.apply(SpanNearQueryDefinition(
      Seq(
        SpanTermQueryDefinition("field1", "value1", Some("name1"), Some(4.0)),
        SpanTermQueryDefinition("field2", "value2", Some("name2"), Some(7.0))
      ),
      slop = 42, boost = Some(2.0), inOrder = Some(true), queryName = Some("rootName")
    ))

    val actual = JSON.parseRaw(builder.string())
    val expected = JSON.parseRaw(
      """
        |{
        |   "span_near":{
        |     "clauses":[
        |         {"span_term":{"field1":"value1","boost":4.0,"_name":"name1"}},
        |         {"span_term":{"field2":"value2","boost":7.0,"_name":"name2"}}
        |      ],
        |      "slop":42,
        |      "in_order":true,
        |      "boost":2.0,
        |      "_name":"rootName"
        |    }
        |}""".stripMargin)

    assert(actual === expected)
  }

}
