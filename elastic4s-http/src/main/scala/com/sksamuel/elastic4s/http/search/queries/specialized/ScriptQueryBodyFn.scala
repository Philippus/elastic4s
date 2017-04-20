package com.sksamuel.elastic4s.http.search.queries.specialized

import com.sksamuel.elastic4s.http.ScriptBuilderFn
import com.sksamuel.elastic4s.searches.queries.ScriptQueryDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object ScriptQueryBodyFn {

  def apply(q: ScriptQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("script")
    builder.rawField("script", ScriptBuilderFn(q.script).bytes)
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder.endObject()
    builder
  }
}
