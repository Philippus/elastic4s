package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.exts.OptionImplicits._

case class ReverseNestedAggregationDefinition(name: String,
                                              path: Option[String] = None,
                                              subaggs: Seq[AbstractAggregation] = Nil,
                                              metadata: Map[String, AnyRef] = Map.empty)
  extends AggregationDefinition {

  type T = ReverseNestedAggregationDefinition

  def path(path: String): ReverseNestedAggregationDefinition = copy(path = path.some)

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T =    copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = metadata)
}
