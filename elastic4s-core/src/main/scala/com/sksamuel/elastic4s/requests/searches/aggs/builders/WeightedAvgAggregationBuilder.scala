package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.script.ScriptBuilderFn
import com.sksamuel.elastic4s.requests.searches.aggs.WeightedAvgAggregation

object WeightedAvgAggregationBuilder {
  def apply(agg: WeightedAvgAggregation): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("weighted_avg")
    agg.weight.foreach { weight =>
      builder.startObject("weight")
      weight.field.foreach(builder.field("field", _))
      weight.script.foreach(script => builder.rawField("script", ScriptBuilderFn(script)))
      builder.endObject()
    }
    agg.value.foreach { value =>
      builder.startObject("value")
      value.field.foreach(builder.field("field", _))
      value.script.foreach(script => builder.rawField("script", ScriptBuilderFn(script)))
      builder.endObject()
    }
    builder.endObject()
    builder.endObject()
  }
}
