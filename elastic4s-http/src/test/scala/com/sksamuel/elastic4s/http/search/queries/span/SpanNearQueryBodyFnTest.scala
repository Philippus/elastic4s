package com.sksamuel.elastic4s.http.search.queries.span

import com.sksamuel.elastic4s.searches.queries.span.{SpanNearQuery, SpanTermQuery}
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

    builder.string() shouldBe ""
  }

}
