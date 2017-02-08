package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.searches.queries.term.TermQueryDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object TermQueryBodyFn {
  def apply(t: TermQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("term")
    builder.startObject(t.field)
    t.boost.map(_.toString).foreach(builder.field("boost", _))
    t.queryName.foreach(builder.field("_name", _))
    builder.field("value", t.value)
    builder.endObject()
    builder.endObject()
    builder.endObject()
  }
}
