package com.sksamuel.elastic4s.requests.searches.queries.span

import com.sksamuel.elastic4s.handlers.searches.queries.span.SpanNearQueryBodyFn
import com.sksamuel.elastic4s.requests.searches.span.{SpanNearQuery, SpanTermQuery}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SpanNearQueryBodyFnTest extends AnyFunSuite with Matchers {

  test("SpanNearQueryBodyFn apply should return appropriate XContentBuilder") {
    val builder = SpanNearQueryBodyFn.apply(SpanNearQuery(
      Seq(
        SpanTermQuery("field1", "value1", Some("name1"), Some(4.0)),
        SpanTermQuery("field2", "value2", Some("name2"), Some(7.0))
      ),
      slop = 42, boost = Some(2.0), inOrder = Some(true), queryName = Some("rootName")
    ))

    builder.string() shouldBe """{"span_near":{"clauses":[{"span_term":{"field1":"value1","boost":4.0,"_name":"name1"}},{"span_term":{"field2":"value2","boost":7.0,"_name":"name2"}}],"slop":42,"in_order":true,"boost":2.0,"_name":"rootName"}}"""
  }

}
