package com.sksamuel.elastic4s.searches.aggs.pipeline

import scala.concurrent.duration.FiniteDuration

case class DerivativeDefinition(name: String,
                                bucketsPath: String,
                                format: Option[String] = None,
                                gapPolicy: Option[GapPolicy] = None,
                                unit: Option[FiniteDuration] = None,
                                unitString: Option[String] = None,
                                metadata: Map[String, AnyRef] = Map.empty)
    extends PipelineAggregationDefinition {

  type T = DerivativeDefinition

  def unit(unit: FiniteDuration): DerivativeDefinition = copy(unit = Some(unit))

  @deprecated("use unit(duration)", "6.0.0")
  def unit(unit: String): DerivativeDefinition = copy(unitString = Some(unit))

  def format(format: String): DerivativeDefinition                  = copy(format = Some(format))
  def gapPolicy(gapPolicy: GapPolicy): DerivativeDefinition         = copy(gapPolicy = Some(gapPolicy))
  def metadata(metadata: Map[String, AnyRef]): DerivativeDefinition = copy(metadata = metadata)
}
