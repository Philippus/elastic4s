package com.sksamuel.elastic4s.requests.searches.queries.span

import com.sksamuel.elastic4s.requests.searches.queries.QueryBuilderFn
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object SpanNearQueryBodyFn {
  def apply(q: SpanNearQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()

    builder.startObject("span_near")
    builder.startArray("clauses")
    q.clauses.foreach { clause =>
      builder.rawValue(QueryBuilderFn(clause))
    }
    builder.endArray()

    builder.field("slop", q.slop)
    q.inOrder.foreach(builder.field("in_order", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))

    builder.endObject()
    builder.endObject()
  }
}
