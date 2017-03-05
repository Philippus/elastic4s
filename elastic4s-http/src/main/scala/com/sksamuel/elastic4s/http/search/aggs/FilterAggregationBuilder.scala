package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.searches.aggs.FilterAggregationDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object FilterAggregationBuilder {
  def apply(agg: FilterAggregationDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.rawField("filter", QueryBuilderFn(agg.query).bytes)
    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
