package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.aggs.GlobalAggregationDefinition

object GlobalAggregationBuilder {
  def apply(agg: GlobalAggregationDefinition): XContentBuilder = {

    val builder = XContentFactory.obj.startObject("global")

    builder.endObject()

    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
