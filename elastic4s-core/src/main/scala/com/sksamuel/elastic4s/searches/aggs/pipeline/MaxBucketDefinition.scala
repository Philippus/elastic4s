package com.sksamuel.elastic4s.searches.aggs.pipeline

import org.elasticsearch.search.aggregations.pipeline.BucketHelpers.GapPolicy

case class MaxBucketDefinition(name: String,
                               bucketsPath: String,
                               format: Option[String] = None,
                               gapPolicy: Option[GapPolicy] = None,
                               metadata: Map[String, AnyRef] = Map.empty) extends PipelineAggregationDefinition {

  type T = MaxBucketDefinition

  def format(format: String): MaxBucketDefinition = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): MaxBucketDefinition = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): MaxBucketDefinition = copy(metadata = metadata)
}
