package com.sksamuel.elastic4s.searches.aggs

trait AbstractAggregation {
  type T <: AbstractAggregation
  def name: String
  def metadata: Map[String, AnyRef]
  def metadata(map: Map[String, AnyRef]): T
}

trait AggregationDefinition extends AbstractAggregation {

  def subaggs: Seq[AbstractAggregation]

  def subagg(agg: AbstractAggregation): T = subaggs(agg)
  def subaggs(first: AbstractAggregation, rest: AbstractAggregation*): T = subaggs(first +: rest)
  def subaggs(aggs: Iterable[AbstractAggregation]): T = subAggregations(aggs)

  def subAggregation(agg: AbstractAggregation): T = subAggregations(agg)
  def subAggregations(first: AbstractAggregation, rest: AbstractAggregation*): T = subAggregations(first +: rest)
  def subAggregations(aggs: Iterable[AbstractAggregation]): T
}
