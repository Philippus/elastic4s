package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.searches.queries.QueryDefinition

case class FilterAggregationDefinition(name: String,
                                       query: QueryDefinition,
                                       subaggs: Seq[AbstractAggregation] = Nil,
                                       metadata: Map[String, AnyRef] = Map.empty)
  extends AggregationDefinition {

  type T = FilterAggregationDefinition

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = metadata)
}
