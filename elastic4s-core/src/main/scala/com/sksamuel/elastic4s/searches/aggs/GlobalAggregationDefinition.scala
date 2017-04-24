package com.sksamuel.elastic4s.searches.aggs

case class GlobalAggregationDefinition(name: String,
                                       subaggs: Seq[AbstractAggregation] = Nil,
                                       metadata: Map[String, AnyRef] = Map.empty) extends AggregationDefinition {

  type T = GlobalAggregationDefinition

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = metadata)
}
