package com.sksamuel.elastic4s.http.search.queries.nested

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.ParentIdQueryDefinition

object ParentIdQueryBodyFn {
  def apply(q: ParentIdQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject().startObject("parent_id")
    builder.field("type", q.`type`)
    builder.field("id", q.id)
    q.ignoreUnmapped.foreach(builder.field("ignore_unmapped", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject().endObject()
  }
}
