package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.exts.OptionImplicits._

case class SumAggregation(name: String,
                          field: Option[String] = None,
                          missing: Option[AnyRef] = None,
                          script: Option[Script] = None,
                          subaggs: Seq[AbstractAggregation] = Nil,
                          metadata: Map[String, AnyRef] = Map.empty)
    extends Aggregation {

  type T = SumAggregation

  def field(field: String): SumAggregation     = copy(field = field.some)
  def missing(missing: AnyRef): SumAggregation = copy(missing = missing.some)
  def script(script: Script): SumAggregation   = copy(script = script.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T                   = copy(metadata = map)
}
