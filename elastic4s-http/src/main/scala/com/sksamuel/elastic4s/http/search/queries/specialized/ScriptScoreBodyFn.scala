package com.sksamuel.elastic4s.http.search.queries.specialized

import com.sksamuel.elastic4s.http.ScriptBuilderFn
import com.sksamuel.elastic4s.searches.queries.funcscorer.ScriptScoreDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object ScriptScoreBodyFn {
  def apply(d: ScriptScoreDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.rawField("script", ScriptBuilderFn(d.script).bytes())
    builder.endObject()
    builder
  }
}
