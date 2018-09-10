package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.searches.aggs.ReverseNestedAggregationDefinition
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object ReverseNestedAggregationBuilder {
  def apply(agg: ReverseNestedAggregationDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("reverse_nested")
    agg.path.foreach(builder.field("path", _))
    builder.endObject()
    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
