package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object SamplerAggregationBuilder {
  def apply(agg: SamplerAggregation): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("sampler")
    agg.shardSize.foreach(builder.field("shard_size", _))
    builder.endObject()

    SubAggsBuilderFn(agg, builder)
    builder.endObject()
  }
}
