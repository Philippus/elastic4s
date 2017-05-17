package com.sksamuel.elastic4s.searches.aggs.pipeline

import com.sksamuel.elastic4s.EnumConversions
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.percentile.PercentilesBucketPipelineAggregationBuilder

import scala.collection.JavaConverters._

object PercentilesBucketPipelineBuilder {

  def apply(p: PercentilesBucketDefinition): PercentilesBucketPipelineAggregationBuilder = {
    val builder = PipelineAggregatorBuilders.percentilesBucket(p.name, p.bucketsPath)
    if (p.metadata.nonEmpty) builder.setMetaData(p.metadata.asJava)
    p.format.foreach(builder.format)
    p.gapPolicy.map(EnumConversions.gapPolicy).foreach(builder.gapPolicy)
    if (p.percents.nonEmpty) {
      val doubles: Array[Double] = p.percents.map(d => d: Double).toArray
      builder.percents(doubles)
    }
    builder
  }
}
