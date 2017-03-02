package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.http.ScriptBuilderFn
import com.sksamuel.elastic4s.searches.aggs.{SumAggregationDefinition, ValueCountAggregationDefinition}
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object SumAggregationBuilder {
  def apply(agg: SumAggregationDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("sum")
    agg.field.foreach(builder.field("field", _))
    agg.missing.foreach(builder.field("missing", _))
    agg.script.foreach { script =>
      builder.rawField("script", ScriptBuilderFn(script).bytes)
    }
    builder.endObject()
    builder.endObject()
  }
}

object ValueCountAggregationBuilder {
  def apply(agg: ValueCountAggregationDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("value_count")
    agg.field.foreach(builder.field("field", _))
    agg.script.foreach { script =>
      builder.rawField("script", ScriptBuilderFn(script).bytes)
    }
    builder.endObject()
    builder.endObject()
  }
}
