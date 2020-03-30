package com.sksamuel.elastic4s.requests.searches.queries.span

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.queries.QueryBuilderFn

object SpanMultiTermQueryBodyFn {
  def apply(q: SpanMultiTermQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("span_multi")
    builder.rawField("match", QueryBuilderFn(q.query))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder.endObject()
  }
}
