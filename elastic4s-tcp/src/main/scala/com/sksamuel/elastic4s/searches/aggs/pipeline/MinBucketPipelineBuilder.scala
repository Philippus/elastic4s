package com.sksamuel.elastic4s.searches.aggs.pipeline

import com.sksamuel.elastic4s.EnumConversions
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.min.MinBucketPipelineAggregationBuilder

object MinBucketPipelineBuilder {

  import scala.collection.JavaConverters._

  def apply(p: MinBucketDefinition): MinBucketPipelineAggregationBuilder = {
    val builder = PipelineAggregatorBuilders.minBucket(p.name, p.bucketsPath)
    if (p.metadata.nonEmpty) builder.setMetaData(p.metadata.asJava)
    p.format.foreach(builder.format)
    p.gapPolicy.map(EnumConversions.gapPolicy).foreach(builder.gapPolicy)
    builder
  }
}
