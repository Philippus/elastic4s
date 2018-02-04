package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.script.Script
import com.sksamuel.elastic4s.searches.aggs.pipeline.PipelineAggregationDefinition
import com.sksamuel.exts.OptionImplicits._

case class AvgAggregation(name: String,
                          field: Option[String] = None,
                          missing: Option[AnyRef] = None,
                          script: Option[Script] = None,
                          pipelines: Seq[PipelineAggregationDefinition] = Nil,
                          subaggs: Seq[AbstractAggregation] = Nil,
                          metadata: Map[String, AnyRef] = Map.empty)
    extends Aggregation {

  type T = AvgAggregation

  def field(field: String): T     = copy(field = field.some)
  def missing(missing: AnyRef): T = copy(missing = missing.some)
  def script(script: Script): T   = copy(script = script.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T                   = copy(metadata = map)
}
