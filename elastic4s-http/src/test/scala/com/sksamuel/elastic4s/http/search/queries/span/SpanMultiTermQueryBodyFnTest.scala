package com.sksamuel.elastic4s.http.search.queries.span

import com.sksamuel.elastic4s.searches.queries.PrefixQuery
import com.sksamuel.elastic4s.searches.queries.span.SpanMultiTermQuery
import org.scalatest.{FunSuite, Matchers}

class SpanMultiTermQueryBodyFnTest extends FunSuite with Matchers {

  test("SpanMultiTermQueryBodyFn apply should return appropriate XContentBuilder") {
    val builder = SpanMultiTermQueryBodyFn.apply(SpanMultiTermQuery(
      PrefixQuery("user", "ki"),
      boost = Some(2.0),
      queryName = Some("rootName")
    ))
    builder.string() shouldBe ""
  }
}
