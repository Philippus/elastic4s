package com.sksamuel.elastic4s.searches.aggs.pipeline

import org.elasticsearch.search.aggregations.pipeline.BucketHelpers.GapPolicy
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders
import org.elasticsearch.search.aggregations.pipeline.bucketmetrics.percentile.PercentilesBucketPipelineAggregationBuilder
import scala.collection.JavaConverters._

case class PercentilesBucketDefinition(name: String,
                                       bucketsPath: String,
                                       format: Option[String] = None,
                                       gapPolicy: Option[GapPolicy] = None,
                                       percents: Seq[Double] = Nil,
                                       metadata: Map[String, AnyRef] = Map.empty) extends PipelineAggregationDefinition {

  type T = PercentilesBucketPipelineAggregationBuilder

  def builder: T = {
    val builder = PipelineAggregatorBuilders.percentilesBucket(name, bucketsPath)
    if (metadata.nonEmpty) builder.setMetaData(metadata.asJava)
    format.foreach(builder.format)
    gapPolicy.foreach(builder.gapPolicy)
    if (percents.nonEmpty) {
      val doubles: Array[Double] = percents.map(d => d: Double).toArray
      builder.percents(doubles)
    }
    builder
  }

  def format(format: String): PercentilesBucketDefinition = copy(format = Some(format))
  def percents(first: Double, rest: Double*): PercentilesBucketDefinition = percents(first +: rest)
  def percents(percents: Seq[Double]): PercentilesBucketDefinition = copy(percents = percents)
  def gapPolicy(gapPolicy: GapPolicy): PercentilesBucketDefinition = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): PercentilesBucketDefinition = copy(metadata = metadata)
}
