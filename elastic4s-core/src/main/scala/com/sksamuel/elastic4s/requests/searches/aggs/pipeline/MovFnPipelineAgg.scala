package com.sksamuel.elastic4s.requests.searches.aggs.pipeline

case class MovFnPipelineAgg(
    name: String,
    bucketsPath: String,
    script: String,
    window: Integer,
    gapPolicy: Option[GapPolicy] = None,
    shift: Option[Integer] = None,
    metadata: Map[String, AnyRef] = Map.empty
) extends PipelineAgg {

  type T = MovFnPipelineAgg

  def shift(shift: Integer): MovFnPipelineAgg                   = copy(shift = Some(shift))
  def gapPolicy(gapPolicy: GapPolicy): MovFnPipelineAgg         = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): MovFnPipelineAgg = copy(metadata = metadata)
}
