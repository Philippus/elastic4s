package com.sksamuel.elastic4s.requests.searches.queries.span

import com.sksamuel.elastic4s.requests.searches.queries.QueryBuilderFn
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object SpanNotQueryBodyFn {
  def apply(q: SpanNotQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("span_not")
    builder.rawField("include", QueryBuilderFn(q.include))
    builder.rawField("exclude", QueryBuilderFn(q.exclude))

    q.pre.foreach(builder.field("pre", _))
    q.post.foreach(builder.field("post", _))
    q.dist.foreach(builder.field("dist", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))

    builder.endObject()
    builder.endObject()
  }
}
