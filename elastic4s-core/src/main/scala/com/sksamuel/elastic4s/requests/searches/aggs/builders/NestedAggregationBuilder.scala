package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.{
  AbstractAggregation,
  AggMetaDataFn,
  NestedAggregation,
  SubAggsBuilderFn
}

object NestedAggregationBuilder {
  def apply(
      agg: NestedAggregation,
      customAggregations: PartialFunction[AbstractAggregation, XContentBuilder]
  ): XContentBuilder = {
    val builder = XContentFactory.obj().startObject("nested")
    builder.field("path", agg.path)
    builder.endObject()
    SubAggsBuilderFn(agg, builder, customAggregations)
    AggMetaDataFn(agg, builder)
    builder
  }
}
