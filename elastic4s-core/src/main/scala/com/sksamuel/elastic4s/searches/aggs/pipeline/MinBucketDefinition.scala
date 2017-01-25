package com.sksamuel.elastic4s.searches.aggs.pipeline

import org.elasticsearch.search.aggregations.pipeline.BucketHelpers.GapPolicy
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.min.MinBucketPipelineAggregationBuilder

import scala.collection.JavaConverters._

case class MinBucketDefinition(name: String,
                               bucketsPath: String,
                               format: Option[String] = None,
                               gapPolicy: Option[GapPolicy] = None,
                               metadata: Map[String, AnyRef] = Map.empty) extends PipelineAggregationDefinition {

  type T = MinBucketPipelineAggregationBuilder

  def builder: T = {
    val builder = PipelineAggregatorBuilders.minBucket(name, bucketsPath)
    if (metadata.nonEmpty) builder.setMetaData(metadata.asJava)
    format.foreach(builder.format)
    gapPolicy.foreach(builder.gapPolicy)
    builder
  }

  def format(format: String): MinBucketDefinition = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): MinBucketDefinition = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): MinBucketDefinition = copy(metadata = metadata)
}
