package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.exts.OptionImplicits._

case class ExtendedStatsAggregation(name: String,
                                    field: Option[String] = None,
                                    script: Option[Script] = None,
                                    missing: Option[Double] = None,
                                    sigma: Option[Double] = None,
                                    subaggs: Seq[AbstractAggregation] = Nil,
                                    metadata: Map[String, AnyRef] = Map.empty)
    extends Aggregation {

  type T = ExtendedStatsAggregation

  def sigma(sigma: Double): ExtendedStatsAggregation     = copy(sigma = sigma.some)
  def field(field: String): ExtendedStatsAggregation     = copy(field = field.some)
  def script(script: Script): ExtendedStatsAggregation   = copy(script = script.some)
  def missing(missing: Double): ExtendedStatsAggregation = copy(missing = missing.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T                   = copy(metadata = map)
}
