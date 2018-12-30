package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.searches.queries.Query

case class FilterAggregation(name: String,
                             query: Query,
                             subaggs: Seq[AbstractAggregation] = Nil,
                             metadata: Map[String, AnyRef] = Map.empty)
    extends Aggregation {

  type T = FilterAggregation

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)
  override def metadata(map: Map[String, AnyRef]): T                   = copy(metadata = map)
}
