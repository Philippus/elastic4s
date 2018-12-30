package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.requests.script.ScriptBuilderFn
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object ScriptQueryBodyFn {

  def apply(q: ScriptQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("script")
    builder.rawField("script", ScriptBuilderFn(q.script))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder.endObject()
    builder
  }
}
