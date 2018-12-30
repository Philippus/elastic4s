package com.sksamuel.elastic4s.requests.searches.queries.span

import com.sksamuel.elastic4s.requests.searches.queries.QueryBuilderFn
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object SpanFirstQueryBodyFn {
  def apply(q: SpanFirstQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("span_first")
    builder.rawField("match", QueryBuilderFn(q.query))
    builder.field("end", q.end)
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder.endObject()
  }
}
