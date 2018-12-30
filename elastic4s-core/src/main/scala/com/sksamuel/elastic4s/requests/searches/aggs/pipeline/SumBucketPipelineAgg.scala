package com.sksamuel.elastic4s.requests.searches.aggs.pipeline

case class SumBucketPipelineAgg(name: String,
                                bucketsPath: String,
                                format: Option[String] = None,
                                gapPolicy: Option[GapPolicy] = None,
                                metadata: Map[String, AnyRef] = Map.empty)
    extends PipelineAgg {

  type T = SumBucketPipelineAgg

  def format(format: String): SumBucketPipelineAgg                  = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): SumBucketPipelineAgg         = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): SumBucketPipelineAgg = copy(metadata = metadata)
}
