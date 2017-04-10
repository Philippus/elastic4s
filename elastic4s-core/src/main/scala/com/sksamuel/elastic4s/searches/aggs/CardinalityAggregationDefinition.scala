package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.exts.OptionImplicits._

case class CardinalityAggregationDefinition(name: String,
                                            field: Option[String] = None,
                                            missing: Option[String] = None,
                                            script: Option[ScriptDefinition] = None,
                                            precisionThreshold: Option[Long] = None,
                                            subaggs: Seq[AbstractAggregation] = Nil,
                                            metadata: Map[String, AnyRef] = Map.empty)
  extends AggregationDefinition {

  type T = CardinalityAggregationDefinition

  def field(field: String): T = copy(field = field.some)
  def missing(missing: String): T = copy(missing = missing.some)
  def script(script: ScriptDefinition): T = copy(script = script.some)
  def precisionThreshold(threshold: Long): T = copy(precisionThreshold = threshold.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = metadata)
}
