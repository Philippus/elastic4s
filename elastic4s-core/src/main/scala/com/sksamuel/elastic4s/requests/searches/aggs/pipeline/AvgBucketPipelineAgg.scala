package com.sksamuel.elastic4s.requests.searches.aggs.pipeline

case class AvgBucketPipelineAgg(name: String,
                                bucketsPath: String,
                                gapPolicy: Option[GapPolicy] = None,
                                format: Option[String] = None,
                                metadata: Map[String, AnyRef] = Map.empty)
    extends PipelineAgg {

  type T = AvgBucketPipelineAgg

  def format(format: String): AvgBucketPipelineAgg          = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): AvgBucketPipelineAgg = copy(gapPolicy = Some(gapPolicy))

  def metadata(metadata: Map[String, AnyRef]): AvgBucketPipelineAgg = copy(metadata = metadata)
}
