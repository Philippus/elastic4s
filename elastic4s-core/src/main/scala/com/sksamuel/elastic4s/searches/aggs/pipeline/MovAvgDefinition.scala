package com.sksamuel.elastic4s.searches.aggs.pipeline

case class MovAvgDefinition(name: String,
                            bucketsPath: String,
                            format: Option[String] = None,
                            gapPolicy: Option[GapPolicy] = None,
                            minimise: Option[Boolean] = None,
                            numPredictions: Option[Integer] = None,
                            settings: Map[String, AnyRef] = Map.empty,
                            window: Option[Integer] = None,
                            metadata: Map[String, AnyRef] = Map.empty) extends PipelineAggregationDefinition {

  type T = MovAvgDefinition

  def minimise(minimise: Boolean): MovAvgDefinition = copy(minimise = Some(minimise))
  def numPredictions(numPredictions: Integer): MovAvgDefinition = copy(numPredictions = Some(numPredictions))
  def settings(format: Map[String, AnyRef]): MovAvgDefinition = copy(settings = settings)
  def window(window: Integer): MovAvgDefinition = copy(window = Some(window))

  def format(format: String): MovAvgDefinition = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): MovAvgDefinition = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): MovAvgDefinition = copy(metadata = metadata)
}
