package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.exts.OptionImplicits._

case class ValueCountAggregationDefinition(name: String,
                                           field: Option[String] = None,
                                           script: Option[ScriptDefinition] = None,
                                           subaggs: Seq[AbstractAggregation] = Nil,
                                           metadata: Map[String, AnyRef] = Map.empty)
  extends AggregationDefinition {

  type T = ValueCountAggregationDefinition

  def field(field: String): ValueCountAggregationDefinition = copy(field = field.some)
  def script(script: ScriptDefinition): ValueCountAggregationDefinition = copy(script = script.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = metadata)
}
