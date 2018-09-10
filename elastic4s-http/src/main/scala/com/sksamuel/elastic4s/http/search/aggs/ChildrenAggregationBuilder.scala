package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.searches.aggs.ChildrenAggregationDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object ChildrenAggregationBuilder {
  def apply(agg: ChildrenAggregationDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("children")
    builder.field("type", agg.childType)
    builder.endObject()
    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
