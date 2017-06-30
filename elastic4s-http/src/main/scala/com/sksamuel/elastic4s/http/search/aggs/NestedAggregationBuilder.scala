package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.json.XContentBuilder
import com.sksamuel.elastic4s.json.XContentFactory
import com.sksamuel.elastic4s.searches.aggs.NestedAggregationDefinition

object NestedAggregationBuilder {
  def apply(agg: NestedAggregationDefinition): XContentBuilder = {
    val builder = XContentFactory.obj().startObject("nested")
    builder.field("path", agg.path)
    builder.endObject()
    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder
  }
}
