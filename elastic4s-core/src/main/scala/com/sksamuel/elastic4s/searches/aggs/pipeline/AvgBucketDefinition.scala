package com.sksamuel.elastic4s.searches.aggs.pipeline

import org.elasticsearch.search.aggregations.pipeline.BucketHelpers.GapPolicy

case class AvgBucketDefinition(name: String,
                               bucketsPath: String,
                               gapPolicy: Option[GapPolicy] = None,
                               format: Option[String] = None,
                               metadata: Map[String, AnyRef] = Map.empty) extends PipelineAggregationDefinition {

  type T = AvgBucketDefinition

  def format(format: String): AvgBucketDefinition = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): AvgBucketDefinition = copy(gapPolicy = Some(gapPolicy))

  def metadata(metadata: Map[String, AnyRef]): AvgBucketDefinition = copy(metadata = metadata)
}
