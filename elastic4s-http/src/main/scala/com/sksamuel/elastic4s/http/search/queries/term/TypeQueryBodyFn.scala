package com.sksamuel.elastic4s.http.search.queries.term

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.TypeQueryDefinition

object TypeQueryBodyFn {
  def apply(q: TypeQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject().startObject("type")
    builder.field("value", q.`type`)
    builder.endObject().endObject()
  }
}
