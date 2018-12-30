package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.script.ScriptBuilderFn
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object StatsAggregationBuilder {
  def apply(agg: StatsAggregation): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject("stats")
    agg.field.foreach(builder.field("field", _))
    agg.missing.foreach(builder.autofield("missing", _))
    agg.format.foreach(builder.field("format", _))
    agg.script.foreach { script =>
      builder.rawField("script", ScriptBuilderFn(script))
    }
    builder
  }
}
