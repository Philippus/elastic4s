package com.sksamuel.elastic4s.searches.aggs.pipeline

import org.elasticsearch.search.aggregations.pipeline.BucketHelpers.GapPolicy

case class StatsBucketDefinition(name: String,
                                 bucketsPath: String,
                                 format: Option[String] = None,
                                 gapPolicy: Option[GapPolicy] = None,
                                 metadata: Map[String, AnyRef] = Map.empty) extends PipelineAggregationDefinition {

  type T = StatsBucketDefinition

  def format(format: String): StatsBucketDefinition = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): StatsBucketDefinition = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): StatsBucketDefinition = copy(metadata = metadata)
}
