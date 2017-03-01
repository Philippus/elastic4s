package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.http.ScriptBuilderFn
import com.sksamuel.elastic4s.searches.aggs.{MaxAggregationDefinition, MinAggregationDefinition, SumAggregationDefinition}
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

object MaxAggregationBuilder {
  def apply(agg: MaxAggregationDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("max")
    agg.field.foreach(builder.field("field", _))
    agg.missing.foreach(builder.field("missing", _))
    agg.script.foreach { script =>
      builder.rawField("script", ScriptBuilderFn(script).bytes)
    }
    builder.endObject()
    builder.endObject()
  }
}

object MinAggregationBuilder {
  def apply(agg: MinAggregationDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("min")
    agg.field.foreach(builder.field("field", _))
    agg.missing.foreach(builder.field("missing", _))
    agg.script.foreach { script =>
      builder.rawField("script", ScriptBuilderFn(script).bytes)
    }
    builder.endObject()
    builder.endObject()
  }
}
