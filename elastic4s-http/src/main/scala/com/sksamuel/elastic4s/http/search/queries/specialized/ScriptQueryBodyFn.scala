package com.sksamuel.elastic4s.http.search.queries.specialized

import com.sksamuel.elastic4s.http.ScriptBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.ScriptQuery

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
