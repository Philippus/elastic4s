package com.sksamuel.elastic4s.handlers.searches.queries.term

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.queries.ExistsQuery

object ExistsQueryBodyFn {
  def apply(q: ExistsQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("exists")
    builder.field("field", q.field)
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder.endObject()
    builder
  }
}
