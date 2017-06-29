package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.searches.aggs.{FilterAggregationDefinition, MissingAggregationDefinition}
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory, XContentType}

object FilterAggregationBuilder {
  def apply(agg: FilterAggregationDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.rawField("filter", QueryBuilderFn(agg.query).bytes, XContentType.JSON)
    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}

object MissingAggregationBuilder {
  def apply(agg: MissingAggregationDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("missing")
    builder.field("field", agg.field.get)
    builder.endObject()
    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
