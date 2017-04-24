package com.sksamuel.elastic4s.searches.aggs.pipeline

import org.elasticsearch.search.aggregations.pipeline.BucketHelpers.GapPolicy

case class ExtendedStatsBucketDefinition(name: String,
                                         bucketsPath: String,
                                         format: Option[String] = None,
                                         gapPolicy: Option[GapPolicy] = None,
                                         metadata: Map[String, AnyRef] = Map.empty) extends PipelineAggregationDefinition {

  type T = ExtendedStatsBucketDefinition

  def format(format: String): ExtendedStatsBucketDefinition = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): ExtendedStatsBucketDefinition = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): ExtendedStatsBucketDefinition = copy(metadata = metadata)
}
