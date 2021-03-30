package com.sksamuel.elastic4s.handlers.searches.queries.nested

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.queries.ParentIdQuery

object ParentIdQueryBodyFn {
  def apply(q: ParentIdQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject("parent_id")
    builder.field("type", q.`type`)
    builder.field("id", q.id)
    q.ignoreUnmapped.foreach(builder.field("ignore_unmapped", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject().endObject()
  }
}
