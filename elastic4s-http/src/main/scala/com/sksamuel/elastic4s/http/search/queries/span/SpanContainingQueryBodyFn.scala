package com.sksamuel.elastic4s.http.search.queries.span

import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.span.SpanContainingQuery

object SpanContainingQueryBodyFn {

  def apply(q: SpanContainingQuery): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject("span_containing")

    builder.rawField("little", QueryBuilderFn(q.little))
    builder.rawField("big", QueryBuilderFn(q.big))

    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))

    builder.endObject()
    builder.endObject()
  }
}
