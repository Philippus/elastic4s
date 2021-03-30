package com.sksamuel.elastic4s.requests.searches.aggs

trait AbstractAggregation {
  type T <: AbstractAggregation
  def name: String
  def metadata: Map[String, AnyRef]
  def metadata(map: Map[String, AnyRef]): T
}

trait Aggregation extends AbstractAggregation {

  def subaggs: Seq[AbstractAggregation]

  def addSubagg(agg: AbstractAggregation): T                             = subaggs(subaggs :+ agg)
  def subaggs(first: AbstractAggregation, rest: AbstractAggregation*): T = subaggs(first +: rest)
  def subaggs(aggs: Iterable[AbstractAggregation]): T                    = subAggregations(aggs)

  def addSubAggregation(agg: AbstractAggregation): T                             = subAggregations(subaggs :+ agg)
  def subAggregations(first: AbstractAggregation, rest: AbstractAggregation*): T = subAggregations(first +: rest)
  def subAggregations(aggs: Iterable[AbstractAggregation]): T
}
