package com.sksamuel.elastic4s.requests.searches.aggs

case class ChildrenAggregation(name: String,
                               childType: String,
                               subaggs: Seq[AbstractAggregation] = Nil,
                               metadata: Map[String, AnyRef] = Map.empty)
    extends Aggregation {

  type T = ChildrenAggregation

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): ChildrenAggregation = copy(metadata = metadata)
}
