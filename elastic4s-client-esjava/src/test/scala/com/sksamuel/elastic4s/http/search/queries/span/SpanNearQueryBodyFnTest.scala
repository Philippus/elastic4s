package com.sksamuel.elastic4s.http.search.queries.span

import com.sksamuel.elastic4s.requests.searches.queries.span.{SpanNearQuery, SpanNearQueryBodyFn, SpanTermQuery}
import org.scalatest.{FunSuite, Matchers}

class SpanNearQueryBodyFnTest extends FunSuite with Matchers {

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
