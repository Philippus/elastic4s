package com.sksamuel.elastic4s.requests.searches.aggs.pipeline

import scala.concurrent.duration.FiniteDuration

case class DerivativePipelineAgg(name: String,
                                 bucketsPath: String,
                                 format: Option[String] = None,
                                 gapPolicy: Option[GapPolicy] = None,
                                 unit: Option[FiniteDuration] = None,
                                 unitString: Option[String] = None,
                                 metadata: Map[String, AnyRef] = Map.empty)
    extends PipelineAgg {

  type T = DerivativePipelineAgg

  def unit(unit: FiniteDuration): DerivativePipelineAgg = copy(unit = Some(unit))

  @deprecated("use unit(duration)", "6.0.0")
  def unit(unit: String): DerivativePipelineAgg = copy(unitString = Some(unit))

  def format(format: String): DerivativePipelineAgg                  = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): DerivativePipelineAgg         = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): DerivativePipelineAgg = copy(metadata = metadata)
}
