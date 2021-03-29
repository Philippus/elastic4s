package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.handlers.script
import com.sksamuel.elastic4s.handlers.script.ScriptBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}

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
