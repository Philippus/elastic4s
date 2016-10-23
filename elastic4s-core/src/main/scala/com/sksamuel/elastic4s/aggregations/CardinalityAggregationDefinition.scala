package com.sksamuel.elastic4s.aggregations

import org.elasticsearch.search.aggregations.AggregationBuilders

case class CardinalityAggregationDefinition(name: String)
  extends CardinalityMetricsAggregationDefinition[CardinalityAggregationDefinition] {
  val aggregationBuilder = AggregationBuilders.cardinality(name)
}
