package com.sksamuel.elastic4s.requests.searches.queries.span

import com.sksamuel.elastic4s.handlers.searches.queries
import com.sksamuel.elastic4s.handlers.searches.queries.QueryBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.span.SpanNearQuery

object SpanNearQueryBodyFn {
  def apply(q: SpanNearQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()

    builder.startObject("span_near")
    builder.startArray("clauses")
    q.clauses.foreach { clause =>
      builder.rawValue(queries.QueryBuilderFn(clause))
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
