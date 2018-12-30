package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.exts.OptionImplicits._

case class MissingAggregation(name: String,
                              field: Option[String] = None,
                              subaggs: Seq[AbstractAggregation] = Nil,
                              metadata: Map[String, AnyRef] = Map.empty)
    extends Aggregation {

  type T = MissingAggregation

  def field(field: String): T = copy(field = field.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T                   = copy(metadata = map)
}
