package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.exts.OptionImplicits._

case class CardinalityAggregation(name: String,
                                  field: Option[String] = None,
                                  missing: Option[String] = None,
                                  script: Option[Script] = None,
                                  precisionThreshold: Option[Long] = None,
                                  subaggs: Seq[AbstractAggregation] = Nil,
                                  metadata: Map[String, AnyRef] = Map.empty)
    extends Aggregation {

  type T = CardinalityAggregation

  def field(field: String): T                = copy(field = field.some)
  def missing(missing: String): T            = copy(missing = missing.some)
  def script(script: Script): T              = copy(script = script.some)
  def precisionThreshold(threshold: Long): T = copy(precisionThreshold = threshold.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T                   = copy(metadata = map)
}
