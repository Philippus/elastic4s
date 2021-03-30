package com.sksamuel.elastic4s.handlers.searches.queries.span

import com.sksamuel.elastic4s.handlers.searches.queries
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.span.SpanMultiTermQuery

object SpanMultiTermQueryBodyFn {
  def apply(q: SpanMultiTermQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("span_multi")
    builder.rawField("match", queries.QueryBuilderFn(q.query))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder.endObject()
  }
}
