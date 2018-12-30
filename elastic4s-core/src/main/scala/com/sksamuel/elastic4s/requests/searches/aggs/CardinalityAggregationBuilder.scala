package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.script.ScriptBuilderFn
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object CardinalityAggregationBuilder {
  def apply(agg: CardinalityAggregation): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject("cardinality")
    agg.field.foreach(builder.field("field", _))
    agg.missing.foreach(builder.field("missing", _))
    agg.precisionThreshold.foreach(builder.field("precision_threshold", _))
    agg.script.foreach { script =>
      builder.rawField("script", ScriptBuilderFn(script))
    }
    builder.endObject().endObject()
  }
}
