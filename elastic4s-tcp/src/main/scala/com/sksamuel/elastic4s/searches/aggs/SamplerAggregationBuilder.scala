package com.sksamuel.elastic4s.searches.aggs

import org.elasticsearch.search.aggregations.bucket.sampler.SamplerAggregationBuilder
import org.elasticsearch.search.aggregations.AggregationBuilders

import scala.collection.JavaConverters._

object SamplerAggregationBuilder {

  def apply(agg: SamplerAggregationDefinition): SamplerAggregationBuilder = {

    val builder = AggregationBuilders.sampler(agg.name)
    agg.shardSize.foreach(builder.shardSize)
    SubAggsFn(builder, agg.subaggs)
    if (agg.metadata.nonEmpty) builder.setMetaData(agg.metadata.asJava)

    builder
  }
}
