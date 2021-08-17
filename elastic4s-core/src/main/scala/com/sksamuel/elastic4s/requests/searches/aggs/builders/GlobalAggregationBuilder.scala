package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.{AggMetaDataFn, GlobalAggregation, SubAggsBuilderFn}

object GlobalAggregationBuilder {
  def apply(agg: GlobalAggregation): XContentBuilder = {

    val builder = XContentFactory.obj().startObject("global")

    builder.endObject()

    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
