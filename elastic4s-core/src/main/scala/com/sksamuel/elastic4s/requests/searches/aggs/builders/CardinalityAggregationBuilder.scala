package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.handlers
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.CardinalityAggregation

object CardinalityAggregationBuilder {
  def apply(agg: CardinalityAggregation): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject("cardinality")
    agg.field.foreach(builder.field("field", _))
    agg.missing.foreach(builder.field("missing", _))
    agg.precisionThreshold.foreach(builder.field("precision_threshold", _))
    agg.script.foreach { script =>
      builder.rawField("script", handlers.script.ScriptBuilderFn(script))
    }
    builder.endObject().endObject()
  }
}
