package com.sksamuel.elastic4s.handlers.searches.queries.nested

import com.sksamuel.elastic4s.handlers.searches.queries.QueryBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.queries.HasParentQuery

object HasParentBodyFn {

  def apply(q: HasParentQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("has_parent")
    builder.field("parent_type", q.parentType)
    builder.rawField("query", QueryBuilderFn(q.query))
    q.ignoreUnmapped.foreach(builder.field("ignore_unmapped", _))
    if (q.score)
      builder.field("score", true)
    q.boost.foreach(builder.field("boost", _))
    q.innerHit.foreach(inner => builder.field("inner_hits", InnerHitQueryBodyBuilder.toJson(inner)))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder.endObject()
    builder
  }
}
