package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.{AbstractAggregation, SamplerAggregation, SubAggsBuilderFn}

object SamplerAggregationBuilder {
  def apply(agg: SamplerAggregation, customAggregations: PartialFunction[AbstractAggregation, XContentBuilder]): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("sampler")
    agg.shardSize.foreach(builder.field("shard_size", _))
    builder.endObject()

    SubAggsBuilderFn(agg, builder, customAggregations)
    builder.endObject()
  }
}
