package com.sksamuel.elastic4s.searches.aggs.pipeline

import com.sksamuel.elastic4s.EnumConversions
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.avg.AvgBucketPipelineAggregationBuilder

object AvgBucketPipelineBuilder {

  import scala.collection.JavaConverters._

  def apply(p: AvgBucketDefinition): AvgBucketPipelineAggregationBuilder = {
    val builder = PipelineAggregatorBuilders.avgBucket(p.name, p.bucketsPath)
    p.format.foreach(builder.format)
    if (p.metadata.nonEmpty) builder.setMetaData(p.metadata.asJava)
    p.gapPolicy.map(EnumConversions.gapPolicy).foreach(builder.gapPolicy)
    builder
  }
}
