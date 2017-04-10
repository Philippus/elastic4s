package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.exts.OptionImplicits._

case class StatsAggregationDefinition(name: String,
                                      field: Option[String] = None,
                                      missing: Option[AnyRef] = None,
                                      format: Option[String] = None,
                                      script: Option[ScriptDefinition] = None,
                                      subaggs: Seq[AbstractAggregation] = Nil,
                                      metadata: Map[String, AnyRef] = Map.empty)
  extends AggregationDefinition {

  type T = StatsAggregationDefinition

  def format(format: String): StatsAggregationDefinition = copy(format = format.some)
  def field(field: String): StatsAggregationDefinition = copy(field = field.some)
  def missing(missing: AnyRef): StatsAggregationDefinition = copy(missing = missing.some)
  def script(script: ScriptDefinition): StatsAggregationDefinition = copy(script = script.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = metadata)
}
