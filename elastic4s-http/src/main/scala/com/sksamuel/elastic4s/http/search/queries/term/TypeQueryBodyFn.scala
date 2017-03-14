package com.sksamuel.elastic4s.http.search.queries.term

import com.sksamuel.elastic4s.searches.queries.TypeQueryDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object TypeQueryBodyFn {
  def apply(q: TypeQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("type")
    builder.field("value", q.`type`)
    builder.endObject()
    builder.endObject()
    builder
  }
}
