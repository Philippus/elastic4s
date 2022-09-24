package com.sksamuel.elastic4s.requests.searches.queries.span

import com.sksamuel.elastic4s.handlers.searches.queries.span.SpanMultiTermQueryBodyFn
import com.sksamuel.elastic4s.requests.searches.queries.PrefixQuery
import com.sksamuel.elastic4s.requests.searches.span.SpanMultiTermQuery
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SpanMultiTermQueryBodyFnTest extends AnyFunSuite with Matchers {

  test("SpanMultiTermQueryBodyFn apply should return appropriate XContentBuilder") {
    val builder = SpanMultiTermQueryBodyFn.apply(SpanMultiTermQuery(
      PrefixQuery("user", "ki"),
      boost = Some(2.0),
      queryName = Some("rootName")
    ))
    builder.string shouldBe """{"span_multi":{"match":{"prefix":{"user":{"value":"ki"}}},"boost":2.0,"_name":"rootName"}}"""
  }
}
