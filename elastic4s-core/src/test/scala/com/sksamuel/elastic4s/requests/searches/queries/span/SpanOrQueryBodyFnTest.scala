package com.sksamuel.elastic4s.requests.searches.queries.span

import com.sksamuel.elastic4s.handlers.searches.queries.span.SpanOrQueryBodyFn
import com.sksamuel.elastic4s.requests.searches.span.{SpanOrQuery, SpanTermQuery}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SpanOrQueryBodyFnTest extends AnyFunSuite with Matchers {

  test("SpanOrQueryBodyFn apply should return appropriate XContentBuilder") {
    val builder = SpanOrQueryBodyFn.apply(SpanOrQuery(
      Seq(
        SpanTermQuery("field1", "value1", Some("name1"), Some(4.0)),
        SpanTermQuery("field2", "value2", Some("name2"), Some(7.0))
      ),
      boost = Some(2.0), queryName = Some("rootName")
    ))

    builder.string() shouldBe """{"span_or":{"clauses":[{"span_term":{"field1":"value1","boost":4.0,"_name":"name1"}},{"span_term":{"field2":"value2","boost":7.0,"_name":"name2"}}],"boost":2.0,"_name":"rootName"}}"""
  }
}
