package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.handlers
import com.sksamuel.elastic4s.handlers.script.ScriptBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.StatsAggregation

object StatsAggregationBuilder {
  def apply(agg: StatsAggregation): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject("stats")
    agg.field.foreach(builder.field("field", _))
    agg.missing.foreach(builder.autofield("missing", _))
    agg.format.foreach(builder.field("format", _))
    agg.script.foreach { script =>
      builder.rawField("script", handlers.script.ScriptBuilderFn(script))
    }
    builder
  }
}
