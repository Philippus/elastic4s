package com.sksamuel.elastic4s.handlers.searches.queries.span

import com.sksamuel.elastic4s.handlers.searches.queries
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.span.SpanFieldMaskingQuery

object SpanFieldMaskingQueryBodyFn {
  def apply(q: SpanFieldMaskingQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("span_field_masking")

    builder.field("field", q.field)
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))

    builder.rawField("query", queries.QueryBuilderFn(q.query))

    builder.endObject()
    builder.endObject()
  }
}
