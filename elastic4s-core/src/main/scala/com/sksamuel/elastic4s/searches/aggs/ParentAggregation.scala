package com.sksamuel.elastic4s.searches.aggs

case class ParentAggregation(name: String,
                             parentType: String,
                             subaggs: Seq[AbstractAggregation] = Nil,
                             metadata: Map[String, AnyRef] = Map.empty)
    extends Aggregation {

  type T = ParentAggregation

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): ParentAggregation = copy(metadata = metadata)
}
