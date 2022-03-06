package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.{AggMetaDataFn, SubAggsBuilderFn, VariableWidthAggregation}

object VariableWidthAggregationBuilder {
  def apply(agg: VariableWidthAggregation): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject("variable_width_histogram")
    builder.field("field", agg.field)

    agg.buckets.foreach(builder.field("buckets", _))
    agg.shardSize.foreach(builder.field("shard_size", _))
    agg.initialBuffer.foreach(builder.field("initial_buffer", _))
    agg.missing.map(_.toString).foreach(builder.field("missing", _))
    builder.endObject()

    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
