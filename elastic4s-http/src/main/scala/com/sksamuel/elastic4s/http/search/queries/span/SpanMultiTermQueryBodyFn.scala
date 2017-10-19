package com.sksamuel.elastic4s.http.search.queries.span

import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.span.SpanMultiTermQueryDefinition

object SpanMultiTermQueryBodyFn {
  def apply(q: SpanMultiTermQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("span_multi")
    builder.rawField("match", QueryBuilderFn(q.query))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder.endObject()
  }
}
