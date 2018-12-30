package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.script.ScriptBuilderFn
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object ValueCountAggregationBuilder {
  def apply(agg: ValueCountAggregation): XContentBuilder = {
    val builder = XContentFactory.obj()
    builder.startObject("value_count")
    agg.field.foreach(builder.field("field", _))
    agg.script.foreach { script =>
      builder.rawField("script", ScriptBuilderFn(script))
    }
    builder.endObject().endObject()
  }
}
