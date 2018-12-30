package com.sksamuel.elastic4s.requests.searches.aggs.pipeline

case class StatsBucketPipelineAgg(name: String,
                                  bucketsPath: String,
                                  format: Option[String] = None,
                                  gapPolicy: Option[GapPolicy] = None,
                                  metadata: Map[String, AnyRef] = Map.empty)
    extends PipelineAgg {

  type T = StatsBucketPipelineAgg

  def format(format: String): StatsBucketPipelineAgg                  = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): StatsBucketPipelineAgg         = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): StatsBucketPipelineAgg = copy(metadata = metadata)
}
