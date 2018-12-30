package com.sksamuel.elastic4s.requests.searches.aggs.pipeline

case class ExtendedStatsBucketPipelineAgg(name: String,
                                          bucketsPath: String,
                                          format: Option[String] = None,
                                          gapPolicy: Option[GapPolicy] = None,
                                          metadata: Map[String, AnyRef] = Map.empty)
    extends PipelineAgg {

  type T = ExtendedStatsBucketPipelineAgg

  def format(format: String): ExtendedStatsBucketPipelineAgg                  = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): ExtendedStatsBucketPipelineAgg         = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): ExtendedStatsBucketPipelineAgg = copy(metadata = metadata)
}
