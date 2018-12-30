package com.sksamuel.elastic4s.requests.searches.queries.span

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object SpanTermQueryBodyFn {
  def apply(q: SpanTermQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("span_term")
    builder.autofield(q.field, q.value)
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder.endObject()
  }
}
