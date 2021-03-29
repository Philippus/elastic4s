package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.handlers
import com.sksamuel.elastic4s.handlers.script.ScriptBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.MinAggregation

object MinAggregationBuilder {
  def apply(agg: MinAggregation): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("min")
    agg.field.foreach(builder.field("field", _))
    agg.missing.foreach(builder.autofield("missing", _))
    agg.script.foreach { script =>
      builder.rawField("script", handlers.script.ScriptBuilderFn(script))
    }
    builder.endObject()
    builder.endObject()
  }
}
