package com.sksamuel.elastic4s.searches.aggs.pipeline

import org.elasticsearch.search.aggregations.pipeline.BucketHelpers.GapPolicy
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.stats.StatsBucketPipelineAggregationBuilder

import scala.collection.JavaConverters._

case class StatsBucketDefinition(name: String,
                                 bucketsPath: String,
                                 format: Option[String] = None,
                                 gapPolicy: Option[GapPolicy] = None,
                                 metadata: Map[String, AnyRef] = Map.empty) extends PipelineAggregationDefinition {

  type T = StatsBucketPipelineAggregationBuilder

  def builder: T = {
    val builder = PipelineAggregatorBuilders.statsBucket(name, bucketsPath)
    if (metadata.nonEmpty) builder.setMetaData(metadata.asJava)
    format.foreach(builder.format)
    gapPolicy.foreach(builder.gapPolicy)
    builder
  }

  def format(format: String): StatsBucketDefinition = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): StatsBucketDefinition = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): StatsBucketDefinition = copy(metadata = metadata)
}
