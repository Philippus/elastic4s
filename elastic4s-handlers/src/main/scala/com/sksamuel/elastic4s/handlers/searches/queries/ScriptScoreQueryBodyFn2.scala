package com.sksamuel.elastic4s.handlers.searches.queries

import com.sksamuel.elastic4s.handlers.script.ScriptBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.queries.ScriptScoreQuery

object ScriptScoreQueryBodyFn2 {

  def apply(q: ScriptScoreQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("script_score")
    q.query.foreach(q => builder.rawField("query", QueryBuilderFn(q)))
    q.script.foreach(s => builder.rawField("script", ScriptBuilderFn(s)))
    q.boost.foreach(builder.field("boost", _))
    q.minScore.foreach(builder.field("min_score", _))
    builder.endObject()
    builder.endObject()
    builder
  }

}
