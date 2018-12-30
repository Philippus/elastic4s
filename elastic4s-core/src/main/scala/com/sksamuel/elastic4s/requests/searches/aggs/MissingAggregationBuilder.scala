package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object MissingAggregationBuilder {
  def apply(agg: MissingAggregation): XContentBuilder = {
    val builder = XContentFactory.obj()
    builder.startObject("missing")
    builder.field("field", agg.field.get)
    builder.endObject()
    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
