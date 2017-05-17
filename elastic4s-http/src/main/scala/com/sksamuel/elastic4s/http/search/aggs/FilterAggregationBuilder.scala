package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.aggs.FilterAggregationDefinition

object FilterAggregationBuilder {
  def apply(agg: FilterAggregationDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.rawField("filter", QueryBuilderFn(agg.query))
    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
