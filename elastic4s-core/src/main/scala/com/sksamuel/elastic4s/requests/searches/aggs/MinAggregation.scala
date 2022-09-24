package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.elastic4s.ext.OptionImplicits._

case class MinAggregation(name: String,
                          field: Option[String] = None,
                          format: Option[String] = None,
                          missing: Option[AnyRef] = None,
                          script: Option[Script] = None,
                          subaggs: Seq[AbstractAggregation] = Nil,
                          metadata: Map[String, AnyRef] = Map.empty)
    extends Aggregation {

  type T = MinAggregation

  def field(field: String): T     = copy(field = field.some)
  def format(format: String): T   = copy(format = format.some)
  def missing(missing: AnyRef): T = copy(missing = missing.some)
  def script(script: Script): T   = copy(script = script.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T                   = copy(metadata = map)
}
