package com.sksamuel.elastic4s.searches.aggs.pipeline

import org.elasticsearch.search.aggregations.pipeline.BucketHelpers.GapPolicy

case class PercentilesBucketDefinition(name: String,
                                       bucketsPath: String,
                                       format: Option[String] = None,
                                       gapPolicy: Option[GapPolicy] = None,
                                       percents: Seq[Double] = Nil,
                                       metadata: Map[String, AnyRef] = Map.empty) extends PipelineAggregationDefinition {

  type T = PercentilesBucketDefinition

  def format(format: String): PercentilesBucketDefinition = copy(format = Some(format))
  def percents(first: Double, rest: Double*): PercentilesBucketDefinition = percents(first +: rest)
  def percents(percents: Seq[Double]): PercentilesBucketDefinition = copy(percents = percents)
  def gapPolicy(gapPolicy: GapPolicy): PercentilesBucketDefinition = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): PercentilesBucketDefinition = copy(metadata = metadata)
}
