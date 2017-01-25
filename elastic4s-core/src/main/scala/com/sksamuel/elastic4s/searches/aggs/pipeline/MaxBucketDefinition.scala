package com.sksamuel.elastic4s.searches.aggs.pipeline

import org.elasticsearch.search.aggregations.pipeline.BucketHelpers.GapPolicy
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.max.MaxBucketPipelineAggregationBuilder

import scala.collection.JavaConverters._

case class MaxBucketDefinition(name: String,
                               bucketsPath: String,
                               format: Option[String] = None,
                               gapPolicy: Option[GapPolicy] = None,
                               metadata: Map[String, AnyRef] = Map.empty) extends PipelineAggregationDefinition {

  type T = MaxBucketPipelineAggregationBuilder

  def builder: T = {
    val builder = PipelineAggregatorBuilders.maxBucket(name, bucketsPath)
    if (metadata.nonEmpty) builder.setMetaData(metadata.asJava)
    format.foreach(builder.format)
    gapPolicy.foreach(builder.gapPolicy)
    builder
  }

  def format(format: String): MaxBucketDefinition = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): MaxBucketDefinition = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): MaxBucketDefinition = copy(metadata = metadata)
}
