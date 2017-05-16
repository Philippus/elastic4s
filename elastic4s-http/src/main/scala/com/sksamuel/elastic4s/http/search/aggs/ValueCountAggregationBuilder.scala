package com.sksamuel.elastic4s.http.search.aggs

object ValueCountAggregationBuilder {
  def apply(agg: ValueCountAggregationDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("value_count")
    agg.field.foreach(builder.field("field", _))
    agg.script.foreach { script =>
      builder.rawField("script", ScriptBuilderFn(script))
    }
    builder.endObject()
    builder.endObject()
  }
}
