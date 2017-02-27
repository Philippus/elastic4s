package com.sksamuel.elastic4s.searches.aggs.pipeline

import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.stats.StatsBucketPipelineAggregationBuilder
import scala.collection.JavaConverters._

object StatsBucketPipelineBuilder {

  def apply(p: StatsBucketDefinition): StatsBucketPipelineAggregationBuilder = {
    val builder = PipelineAggregatorBuilders.statsBucket(p.name, p.bucketsPath)
    if (p.metadata.nonEmpty) builder.setMetaData(p.metadata.asJava)
    p.format.foreach(builder.format)
    p.gapPolicy.foreach(builder.gapPolicy)
    builder
  }
}
