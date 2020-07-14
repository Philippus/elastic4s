package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.aggs.ParentAggregation

object ParentAggregationBuilder {
  def apply(agg: ParentAggregation): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("parent")

    builder.field("type", agg.parentType)
    builder.endObject()

    SubAggsBuilderFn(agg, builder)

    builder
  }
}
