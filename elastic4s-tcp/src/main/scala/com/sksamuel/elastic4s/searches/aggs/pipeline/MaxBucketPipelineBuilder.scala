package com.sksamuel.elastic4s.searches.aggs.pipeline

import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.max.MaxBucketPipelineAggregationBuilder

import scala.collection.JavaConverters._

object MaxBucketPipelineBuilder {
  def apply(p: MaxBucketDefinition): MaxBucketPipelineAggregationBuilder = {
    val builder = PipelineAggregatorBuilders.maxBucket(p.name, p.bucketsPath)
    if (p.metadata.nonEmpty) builder.setMetaData(p.metadata.asJava)
    p.format.foreach(builder.format)
    p.gapPolicy.foreach(builder.gapPolicy)
    builder
  }
}
