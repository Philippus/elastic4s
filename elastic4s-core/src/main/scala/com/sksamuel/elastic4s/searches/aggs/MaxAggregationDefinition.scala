package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.exts.OptionImplicits._

case class MaxAggregationDefinition(name: String,
                                    field: Option[String] = None,
                                    format: Option[String] = None,
                                    missing: Option[AnyRef] = None,
                                    script: Option[ScriptDefinition] = None,
                                    subaggs: Seq[AbstractAggregation] = Nil,
                                    metadata: Map[String, AnyRef] = Map.empty)
  extends AggregationDefinition {

  type T = MaxAggregationDefinition

  def field(field: String): T = copy(field = field.some)
  def format(format: String): T = copy(format = format.some)
  def missing(missing: AnyRef): T = copy(missing = missing.some)
  def script(script: ScriptDefinition): T = copy(script = script.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = metadata)
}
