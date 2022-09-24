package com.sksamuel.elastic4s.requests.searches.queries.span

import com.sksamuel.elastic4s.handlers.searches.queries.span.SpanFieldMaskingQueryBodyFn
import com.sksamuel.elastic4s.requests.searches.span.{SpanFieldMaskingQuery, SpanTermQuery}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SpanFieldMaskingQueryBodyFnTest extends AnyFunSuite with Matchers {
  test("SpanFieldMaskingQueryBodyFn apply should return appropriate XContentBuilder") {
    val builder = SpanFieldMaskingQueryBodyFn.apply(SpanFieldMaskingQuery(
      field = "masked_field",
      query = SpanTermQuery("field1", "value1", Some("name1"), Some(4.0)),
      boost = Some(2.0),
      queryName = Some("rootName")
    ))
    builder.string shouldBe """{"span_field_masking":{"field":"masked_field","boost":2.0,"_name":"rootName","query":{"span_term":{"field1":"value1","boost":4.0,"_name":"name1"}}}}"""
  }
}
