package com.sksamuel.elastic4s.requests.searches.aggs.pipeline

case class CumulativeCardinalityPipelineAgg(name: String,
  bucketsPath: String,
  format: Option[String] = None,
  metadata: Map[String, AnyRef] = Map.empty)
  extends PipelineAgg {

  type T = CumulativeCardinalityPipelineAgg

  def format(format: String): CumulativeCardinalityPipelineAgg                  = copy(format = Some(format))
  def metadata(metadata: Map[String, AnyRef]): CumulativeCardinalityPipelineAgg = copy(metadata = metadata)
}
