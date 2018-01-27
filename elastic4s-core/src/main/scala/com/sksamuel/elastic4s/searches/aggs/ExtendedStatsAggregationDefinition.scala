package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.exts.OptionImplicits._

case class ExtendedStatsAggregationDefinition(name: String,
                                              field: Option[String] = None,
                                              script: Option[ScriptDefinition] = None,
                                              missing: Option[Double] = None,
                                              sigma: Option[Double] = None,
                                              subaggs: Seq[AbstractAggregation] = Nil,
                                              metadata: Map[String, AnyRef] = Map.empty)
    extends AggregationDefinition {

  type T = ExtendedStatsAggregationDefinition

  def sigma(sigma: Double): ExtendedStatsAggregationDefinition             = copy(sigma = sigma.some)
  def field(field: String): ExtendedStatsAggregationDefinition             = copy(field = field.some)
  def script(script: ScriptDefinition): ExtendedStatsAggregationDefinition = copy(script = script.some)
  def missing(missing: Double): ExtendedStatsAggregationDefinition         = copy(missing = missing.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T                   = copy(metadata = map)
}
