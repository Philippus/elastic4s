package com.sksamuel.elastic4s.searches.aggs.pipeline

import org.elasticsearch.search.aggregations.pipeline.BucketHelpers.GapPolicy
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.stats.extended.ExtendedStatsBucketPipelineAggregationBuilder

import scala.collection.JavaConverters._

case class ExtendedStatsBucketDefinition(name: String,
                                         bucketsPath: String,
                                         format: Option[String] = None,
                                         gapPolicy: Option[GapPolicy] = None,
                                         metadata: Map[String, AnyRef] = Map.empty) extends PipelineAggregationDefinition {

  type T = ExtendedStatsBucketPipelineAggregationBuilder

  def builder: T = {
    val builder = PipelineAggregatorBuilders.extendedStatsBucket(name, bucketsPath)
    if (metadata.nonEmpty) builder.setMetaData(metadata.asJava)
    format.foreach(builder.format)
    gapPolicy.foreach(builder.gapPolicy)
    builder
  }

  def format(format: String): ExtendedStatsBucketDefinition = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): ExtendedStatsBucketDefinition = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): ExtendedStatsBucketDefinition = copy(metadata = metadata)
}
