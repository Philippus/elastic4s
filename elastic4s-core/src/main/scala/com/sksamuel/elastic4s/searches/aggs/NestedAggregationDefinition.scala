package com.sksamuel.elastic4s.searches.aggs

case class NestedAggregationDefinition(name: String,
                                       path: String,
                                       subaggs: Seq[AbstractAggregation] = Nil,
                                       metadata: Map[String, AnyRef] = Map.empty)
  extends AggregationDefinition {

  type T = NestedAggregationDefinition

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = metadata)
}

