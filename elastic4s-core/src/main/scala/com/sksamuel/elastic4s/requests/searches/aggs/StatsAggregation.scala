package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.exts.OptionImplicits._

case class StatsAggregation(name: String,
                            field: Option[String] = None,
                            missing: Option[AnyRef] = None,
                            format: Option[String] = None,
                            script: Option[Script] = None,
                            subaggs: Seq[AbstractAggregation] = Nil,
                            metadata: Map[String, AnyRef] = Map.empty)
    extends Aggregation {

  type T = StatsAggregation

  def format(format: String): StatsAggregation   = copy(format = format.some)
  def field(field: String): StatsAggregation     = copy(field = field.some)
  def missing(missing: AnyRef): StatsAggregation = copy(missing = missing.some)
  def script(script: Script): StatsAggregation   = copy(script = script.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T                   = copy(metadata = map)
}
