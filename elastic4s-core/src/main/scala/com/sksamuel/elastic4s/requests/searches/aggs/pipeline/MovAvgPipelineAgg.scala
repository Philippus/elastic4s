package com.sksamuel.elastic4s.requests.searches.aggs.pipeline

case class MovAvgPipelineAgg(name: String,
                             bucketsPath: String,
                             format: Option[String] = None,
                             gapPolicy: Option[GapPolicy] = None,
                             minimize: Option[Boolean] = None,
                             numPredictions: Option[Integer] = None,
                             settings: Map[String, Any] = Map.empty,
                             window: Option[Integer] = None,
                             metadata: Map[String, AnyRef] = Map.empty)
    extends PipelineAgg {

  type T = MovAvgPipelineAgg

  def minimize(minimize: Boolean): MovAvgPipelineAgg             = copy(minimize = Some(minimize))
  def numPredictions(numPredictions: Integer): MovAvgPipelineAgg = copy(numPredictions = Some(numPredictions))
  def settings(settings: Map[String, Any]): MovAvgPipelineAgg    = copy(settings = settings)
  def window(window: Integer): MovAvgPipelineAgg                 = copy(window = Some(window))

  def format(format: String): MovAvgPipelineAgg                  = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): MovAvgPipelineAgg         = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): MovAvgPipelineAgg = copy(metadata = metadata)
}
