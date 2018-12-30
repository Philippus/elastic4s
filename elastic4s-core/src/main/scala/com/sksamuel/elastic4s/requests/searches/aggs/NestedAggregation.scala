package com.sksamuel.elastic4s.requests.searches.aggs

case class NestedAggregation(name: String,
                             path: String,
                             subaggs: Seq[AbstractAggregation] = Nil,
                             metadata: Map[String, AnyRef] = Map.empty)
    extends Aggregation {

  type T = NestedAggregation

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T                   = copy(metadata = map)
}
