package com.sksamuel.elastic4s.http.search.queries.span

import com.sksamuel.elastic4s.searches.queries.span.{SpanNearQueryDefinition, SpanOrQueryDefinition, SpanTermQueryDefinition}
import org.scalatest.FunSuite

import scala.util.parsing.json.JSON

class SpanOrQueryBodyFnTest extends FunSuite {

  test("SpanOrQueryBodyFn apply should return appropriate XContentBuilder") {
    val builder = SpanOrQueryBodyFn.apply(SpanOrQueryDefinition(
      Seq(
        SpanTermQueryDefinition("field1", "value1", Some("name1"), Some(4.0)),
        SpanTermQueryDefinition("field2", "value2", Some("name2"), Some(7.0))
      ),
      boost = Some(2.0), queryName = Some("rootName")
    ))

    val actual = JSON.parseRaw(builder.string())
    val expected = JSON.parseRaw(
      """
        |{
        |   "span_or":{
        |     "clauses":[
        |         {"span_term":{"field1":"value1","boost":4.0,"_name":"name1"}},
        |         {"span_term":{"field2":"value2","boost":7.0,"_name":"name2"}}
        |      ],
        |      "boost":2.0,
        |      "_name":"rootName"
        |    }
        |}""".stripMargin)

    assert(actual === expected)
  }

}
