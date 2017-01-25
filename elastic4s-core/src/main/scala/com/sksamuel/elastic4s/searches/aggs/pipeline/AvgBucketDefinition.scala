package com.sksamuel.elastic4s.searches.aggs.pipeline

import org.elasticsearch.search.aggregations.pipeline.BucketHelpers.GapPolicy
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.avg.AvgBucketPipelineAggregationBuilder

import scala.collection.JavaConverters._

case class AvgBucketDefinition(name: String,
                               bucketsPath: String,
                               gapPolicy: Option[GapPolicy] = None,
                               format: Option[String] = None,
                               metadata: Map[String, AnyRef] = Map.empty) extends PipelineAggregationDefinition {

  type T = AvgBucketPipelineAggregationBuilder

  def builder: T = {
    val builder = PipelineAggregatorBuilders.avgBucket(name, bucketsPath)
    format.foreach(builder.format)
    if (metadata.nonEmpty) builder.setMetaData(metadata.asJava)
    gapPolicy.foreach(builder.gapPolicy)
    builder
  }

  def format(format: String): AvgBucketDefinition = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): AvgBucketDefinition = copy(gapPolicy = Some(gapPolicy))

  def metadata(metadata: Map[String, AnyRef]): AvgBucketDefinition = copy(metadata = metadata)
}
