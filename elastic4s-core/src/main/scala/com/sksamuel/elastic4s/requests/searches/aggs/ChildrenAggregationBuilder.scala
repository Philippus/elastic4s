package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object ChildrenAggregationBuilder {
  def apply(agg: ChildrenAggregation): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("children")

    builder.field("type", agg.childType)
    builder.endObject()

    SubAggsBuilderFn(agg, builder)

    builder
  }
}
