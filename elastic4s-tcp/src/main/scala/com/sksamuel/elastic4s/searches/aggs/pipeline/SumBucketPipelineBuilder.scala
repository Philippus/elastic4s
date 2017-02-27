package com.sksamuel.elastic4s.searches.aggs.pipeline

import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.sum.SumBucketPipelineAggregationBuilder
import scala.collection.JavaConverters._

object SumBucketPipelineBuilder {

  def apply(p: SumBucketDefinition): SumBucketPipelineAggregationBuilder = {
    val builder = PipelineAggregatorBuilders.sumBucket(p.name, p.bucketsPath)
    if (p.metadata.nonEmpty) builder.setMetaData(p.metadata.asJava)
    p.format.foreach(builder.format)
    p.gapPolicy.foreach(builder.gapPolicy)
    builder
  }
}
