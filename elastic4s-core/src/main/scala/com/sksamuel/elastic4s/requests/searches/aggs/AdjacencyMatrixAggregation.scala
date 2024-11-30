package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.searches.queries.Query

case class AdjacencyMatrixAggregation(
    name: String,
    filters: Iterable[(String, Query)],
    separator: Option[String] = None,
    subaggs: Seq[AbstractAggregation] = Nil,
    metadata: Map[String, AnyRef] = Map.empty
) extends Aggregation {

  type T = AdjacencyMatrixAggregation

  override def subAggregations(aggs: Iterable[AbstractAggregation]): T = copy(subaggs = aggs.toSeq)

  override def metadata(map: Map[String, AnyRef]): T = copy(metadata = map)

  def separator(sep: String): T = copy(separator = Some(sep))
}
