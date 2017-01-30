package com.sksamuel.elastic4s.http.search

import com.sksamuel.elastic4s.searches.queries.TermQueryDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object TermQueryBodyFn {
  def apply(t: TermQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("term")
    builder.startObject(t.field)
    t.boost.map(_.toString).foreach(builder.field("boost", _))
    builder.field("value", t.value)
    builder.endObject()
    builder.endObject()
    builder.endObject()
  }
}
