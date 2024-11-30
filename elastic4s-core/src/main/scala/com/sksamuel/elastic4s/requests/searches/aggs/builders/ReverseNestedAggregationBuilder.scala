package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.{
  AbstractAggregation,
  AggMetaDataFn,
  ReverseNestedAggregation,
  SubAggsBuilderFn
}

object ReverseNestedAggregationBuilder {
  def apply(
      agg: ReverseNestedAggregation,
      customAggregations: PartialFunction[AbstractAggregation, XContentBuilder]
  ): XContentBuilder = {
    val builder = XContentFactory.obj().startObject("reverse_nested")
    agg.path.foreach(builder.field("path", _))
    builder.endObject()
    SubAggsBuilderFn(agg, builder, customAggregations)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
