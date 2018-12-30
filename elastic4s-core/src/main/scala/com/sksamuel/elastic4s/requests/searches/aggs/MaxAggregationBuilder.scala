package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.script.ScriptBuilderFn
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object MaxAggregationBuilder {
  def apply(agg: MaxAggregation): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("max")
    agg.field.foreach(builder.field("field", _))
    agg.missing.foreach(builder.autofield("missing", _))
    agg.script.foreach { script =>
      builder.rawField("script", ScriptBuilderFn(script))
    }
    builder.endObject()
    builder.endObject()
  }
}
