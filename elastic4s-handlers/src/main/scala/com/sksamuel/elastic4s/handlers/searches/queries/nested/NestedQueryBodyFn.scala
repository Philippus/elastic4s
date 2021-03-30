package com.sksamuel.elastic4s.handlers.searches.queries.nested

import com.sksamuel.elastic4s.EnumConversions
import com.sksamuel.elastic4s.handlers.searches.queries.QueryBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.queries.NestedQuery

object NestedQueryBodyFn {
  def apply(q: NestedQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("nested")
    builder.field("path", q.path)
    q.scoreMode.map(EnumConversions.scoreMode).foreach(builder.field("score_mode", _))
    builder.rawField("query", QueryBuilderFn(q.query))
    q.ignoreUnmapped.foreach(builder.field("ignore_unmapped", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    q.inner.foreach(inner => builder.field("inner_hits", InnerHitQueryBodyBuilder.toJson(inner)))
    builder.endObject()
    builder.endObject()
  }
}
