package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.exts.OptionImplicits._

case class SumAggregationDefinition(name: String,
                                    field: Option[String] = None,
                                    missing: Option[AnyRef] = None,
                                    script: Option[ScriptDefinition] = None,
                                    subaggs: Seq[AbstractAggregation] = Nil,
                                    metadata: Map[String, AnyRef] = Map.empty)
  extends AggregationDefinition {

  type T = SumAggregationDefinition

  def field(field: String): SumAggregationDefinition = copy(field = field.some)
  def missing(missing: AnyRef): SumAggregationDefinition = copy(missing = missing.some)
  def script(script: ScriptDefinition): SumAggregationDefinition = copy(script = script.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = metadata)
}
