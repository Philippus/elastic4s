package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.{AbstractAggregation, ChildrenAggregation, SubAggsBuilderFn}

object ChildrenAggregationBuilder {
  def apply(
      agg: ChildrenAggregation,
      customAggregations: PartialFunction[AbstractAggregation, XContentBuilder]
  ): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("children")

    builder.field("type", agg.childType)
    builder.endObject()

    SubAggsBuilderFn(agg, builder, customAggregations)

    builder
  }
}
