package com.sksamuel.elastic4s.http.search.queries.specialized

import com.sksamuel.elastic4s.searches.queries.funcscorer.ScriptScoreDefinition
import org.elasticsearch.common.bytes.BytesArray
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object ScriptScoreQueryBodyFn {

  def apply(q: ScriptScoreDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.rawField("script", new BytesArray(q.script.script))
    builder
  }

}
