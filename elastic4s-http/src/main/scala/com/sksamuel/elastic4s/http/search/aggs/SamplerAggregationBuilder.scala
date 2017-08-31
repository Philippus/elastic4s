package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.aggs.SamplerAggregationDefinition

object SamplerAggregationBuilder {
  def apply(agg: SamplerAggregationDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("sampler")
    agg.shardSize.foreach(builder.field("shard_size", _))
    builder.endObject()

    SubAggsBuilderFn(agg, builder)
    builder.endObject()
  }
}
