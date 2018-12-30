package com.sksamuel.elastic4s.requests.searches.aggs.pipeline

case class MinBucketPipelineAgg(name: String,
                                bucketsPath: String,
                                format: Option[String] = None,
                                gapPolicy: Option[GapPolicy] = None,
                                metadata: Map[String, AnyRef] = Map.empty)
    extends PipelineAgg {

  type T = MinBucketPipelineAgg

  def format(format: String): MinBucketPipelineAgg                  = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): MinBucketPipelineAgg         = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): MinBucketPipelineAgg = copy(metadata = metadata)
}
