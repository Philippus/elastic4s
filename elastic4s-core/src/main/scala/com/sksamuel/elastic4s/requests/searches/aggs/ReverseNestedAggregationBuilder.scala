package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object ReverseNestedAggregationBuilder {
  def apply(agg: ReverseNestedAggregation): XContentBuilder = {
    val builder = XContentFactory.obj().startObject("reverse_nested")
    agg.path.foreach(builder.field("path", _))
    builder.endObject()
    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
