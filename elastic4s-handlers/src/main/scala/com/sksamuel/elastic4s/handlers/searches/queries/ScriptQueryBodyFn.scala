package com.sksamuel.elastic4s.handlers.searches.queries

import com.sksamuel.elastic4s.handlers.script
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.queries.ScriptQuery

object ScriptQueryBodyFn {

  def apply(q: ScriptQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("script")
    builder.rawField("script", script.ScriptBuilderFn(q.script))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder.endObject()
    builder
  }
}
