package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object GlobalAggregationBuilder {
  def apply(agg: GlobalAggregation): XContentBuilder = {

    val builder = XContentFactory.obj.startObject("global")

    builder.endObject()

    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
