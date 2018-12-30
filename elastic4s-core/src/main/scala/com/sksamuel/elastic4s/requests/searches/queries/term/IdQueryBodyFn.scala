package com.sksamuel.elastic4s.requests.searches.queries.term

import com.sksamuel.elastic4s.requests.searches.queries.IdQuery
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object IdQueryBodyFn {

  def apply(q: IdQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("ids")
    if (q.types.nonEmpty)
      builder.array("type", q.types.toArray)
    builder.autoarray("values", q.ids)
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder.endObject()
    builder
  }
}
