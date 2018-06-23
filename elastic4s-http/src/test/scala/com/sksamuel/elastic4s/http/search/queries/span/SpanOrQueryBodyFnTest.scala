package com.sksamuel.elastic4s.http.search.queries.span

import com.sksamuel.elastic4s.searches.queries.span.{SpanOrQuery, SpanTermQuery}
import org.scalatest.{FunSuite, Matchers}

class SpanOrQueryBodyFnTest extends FunSuite with Matchers {

  test("SpanOrQueryBodyFn apply should return appropriate XContentBuilder") {
    val builder = SpanOrQueryBodyFn.apply(SpanOrQuery(
      Seq(
        SpanTermQuery("field1", "value1", Some("name1"), Some(4.0)),
        SpanTermQuery("field2", "value2", Some("name2"), Some(7.0))
      ),
      boost = Some(2.0), queryName = Some("rootName")
    ))

    builder.string() shouldBe ""
  }
}
