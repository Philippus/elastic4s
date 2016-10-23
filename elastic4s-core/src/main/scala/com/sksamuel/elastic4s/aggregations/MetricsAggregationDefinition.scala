package com.sksamuel.elastic4s.aggregations

trait MetricsAggregationDefinition[+Self <: MetricsAggregationDefinition[Self, B], B <: MetricsAggregationBuilder[B]]
  extends AbstractAggregationDefinition {
  val aggregationBuilder: B

  def builder = aggregationBuilder
}
