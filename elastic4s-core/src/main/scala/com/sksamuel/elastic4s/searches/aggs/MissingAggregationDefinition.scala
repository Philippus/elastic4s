package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.exts.OptionImplicits._

case class MissingAggregationDefinition(name: String,
                                        field: Option[String] = None,
                                        subaggs: Seq[AbstractAggregation] = Nil,
                                        metadata: Map[String, AnyRef] = Map.empty)
  extends AggregationDefinition {

  type T = MissingAggregationDefinition

  def field(field: String): T = copy(field = field.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = metadata)
}
