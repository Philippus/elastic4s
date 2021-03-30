package com.sksamuel.elastic4s.requests.searches.queries.span

import com.sksamuel.elastic4s.handlers.searches.queries.span.SpanFirstQueryBodyFn
import com.sksamuel.elastic4s.requests.searches.span.{SpanFirstQuery, SpanTermQuery}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SpanFirstQueryBodyFnTest extends AnyFunSuite with Matchers {

  test("SpanFirstQueryBodyFn apply should return appropriate XContentBuilder") {
    val builder = SpanFirstQueryBodyFn.apply(SpanFirstQuery(
      SpanTermQuery("field1", "value1"),
      end = 5,
      boost = Some(2.0),
      queryName = Some("rootName")
    ))
    builder.string() shouldBe """{"span_first":{"match":{"span_term":{"field1":"value1"}},"end":5,"boost":2.0,"_name":"rootName"}}"""
  }
}
