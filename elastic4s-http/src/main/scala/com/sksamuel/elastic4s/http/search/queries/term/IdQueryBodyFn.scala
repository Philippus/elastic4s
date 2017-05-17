package com.sksamuel.elastic4s.http.search.queries.term

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.IdQueryDefinition

object IdQueryBodyFn {

  def apply(q: IdQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("ids")
    if (q.types.nonEmpty) {
      builder.array("type", q.types.toArray)
    }
    builder.autoarray("values", q.ids)
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder.endObject()
    builder
  }
}
