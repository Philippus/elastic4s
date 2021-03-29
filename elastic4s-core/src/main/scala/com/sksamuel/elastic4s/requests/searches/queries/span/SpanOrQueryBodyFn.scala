package com.sksamuel.elastic4s.requests.searches.queries.span

import com.sksamuel.elastic4s.handlers.searches.queries
import com.sksamuel.elastic4s.handlers.searches.queries.QueryBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.span.SpanOrQuery

object SpanOrQueryBodyFn {
  def apply(q: SpanOrQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("span_or")
    builder.startArray("clauses")
    q.clauses.foreach { clause =>
      builder.rawValue(queries.QueryBuilderFn(clause))
    }
    builder.endArray()

    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))

    builder.endObject()
    builder.endObject()
  }
}
