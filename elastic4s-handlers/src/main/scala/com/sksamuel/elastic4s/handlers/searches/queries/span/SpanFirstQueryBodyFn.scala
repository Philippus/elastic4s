package com.sksamuel.elastic4s.handlers.searches.queries.span

import com.sksamuel.elastic4s.handlers.searches.queries
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.span.SpanFirstQuery

object SpanFirstQueryBodyFn {
  def apply(q: SpanFirstQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("span_first")
    builder.rawField("match", queries.QueryBuilderFn(q.query))
    builder.field("end", q.end)
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder.endObject()
  }
}
