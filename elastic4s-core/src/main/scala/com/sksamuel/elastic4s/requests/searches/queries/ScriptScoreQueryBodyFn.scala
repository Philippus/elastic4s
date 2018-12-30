package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.requests.searches.queries.funcscorer.ScriptScore
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object ScriptScoreQueryBodyFn {

  def apply(q: ScriptScore): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.rawField("script", q.script.script)
    builder
  }

}
