package com.sksamuel.elastic4s.handlers.searches.queries.span

import com.sksamuel.elastic4s.handlers.searches.queries
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.span.SpanContainingQuery

object SpanContainingQueryBodyFn {

  def apply(q: SpanContainingQuery): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject("span_containing")

    builder.rawField("little", queries.QueryBuilderFn(q.little))
    builder.rawField("big", queries.QueryBuilderFn(q.big))

    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))

    builder.endObject()
    builder.endObject()
  }
}
