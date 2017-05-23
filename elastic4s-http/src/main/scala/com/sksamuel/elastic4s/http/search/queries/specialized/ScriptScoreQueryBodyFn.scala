package com.sksamuel.elastic4s.http.search.queries.specialized

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.funcscorer.ScriptScoreDefinition

object ScriptScoreQueryBodyFn {

  def apply(q: ScriptScoreDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.rawField("script", q.script.script)
    builder
  }

}
