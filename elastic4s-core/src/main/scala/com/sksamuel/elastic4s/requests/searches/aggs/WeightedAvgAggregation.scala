package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.elastic4s.requests.searches.aggs.pipeline.PipelineAgg
import com.sksamuel.elastic4s.ext.OptionImplicits._

case class WeightedAvgAggregation(
    name: String,
    value: Option[WeightedAvgField] = None,
    weight: Option[WeightedAvgField] = None,
    pipelines: Seq[PipelineAgg] = Nil,
    subaggs: Seq[AbstractAggregation] = Nil,
    metadata: Map[String, AnyRef] = Map.empty
) extends Aggregation {
  type T = WeightedAvgAggregation
  def value(value: WeightedAvgField): WeightedAvgAggregation   = copy(value = value.some)
  def weight(weight: WeightedAvgField): WeightedAvgAggregation = copy(weight = weight.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T                   = copy(metadata = map)
}

case class WeightedAvgField(field: Option[String] = None, script: Option[Script] = None, missing: Option[AnyRef] = None)
