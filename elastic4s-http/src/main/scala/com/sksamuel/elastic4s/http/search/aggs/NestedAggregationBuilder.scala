package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.searches.aggs.NestedAggregationDefinition
import org.elasticsearch.common.xcontent.{ToXContent, XContentBuilder, XContentFactory, XContentType}

object NestedAggregationBuilder {
  def apply(agg: NestedAggregationDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("nested")
    builder.field("path", agg.path)
    builder.endObject()
    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
