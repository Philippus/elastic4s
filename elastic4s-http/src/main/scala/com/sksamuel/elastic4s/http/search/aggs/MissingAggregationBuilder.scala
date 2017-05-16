package com.sksamuel.elastic4s.http.search.aggs

object MissingAggregationBuilder {
  def apply(agg: MissingAggregationDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("missing")
    builder.field("field", agg.field.get)
    builder.endObject()
    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
