package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.{ChildrenAggregation, SubAggsBuilderFn}

object ChildrenAggregationBuilder {
  def apply(agg: ChildrenAggregation): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("children")

    builder.field("type", agg.childType)
    builder.endObject()

    SubAggsBuilderFn(agg, builder)

    builder
  }
}
