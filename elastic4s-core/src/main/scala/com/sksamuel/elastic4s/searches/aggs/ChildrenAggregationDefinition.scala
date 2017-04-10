package com.sksamuel.elastic4s.searches.aggs

case class ChildrenAggregationDefinition(name: String,
                                         childType: String,
                                         subaggs: Seq[AbstractAggregation] = Nil,
                                         metadata: Map[String, AnyRef] = Map.empty)
  extends AggregationDefinition {

  type T = ChildrenAggregationDefinition

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): ChildrenAggregationDefinition = copy(metadata = metadata)
}
