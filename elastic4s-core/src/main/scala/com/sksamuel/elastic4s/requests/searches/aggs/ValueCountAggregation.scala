package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.exts.OptionImplicits._

case class ValueCountAggregation(name: String,
                                 field: Option[String] = None,
                                 script: Option[Script] = None,
                                 subaggs: Seq[AbstractAggregation] = Nil,
                                 metadata: Map[String, AnyRef] = Map.empty)
    extends Aggregation {

  type T = ValueCountAggregation

  def field(field: String): ValueCountAggregation   = copy(field = field.some)
  def script(script: Script): ValueCountAggregation = copy(script = script.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T                   = copy(metadata = map)
}
