package com.sksamuel.elastic4s.aggregations

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.min.MinAggregationBuilder

case class MinAggregationDefinition(name: String)
  extends ValuesSourceMetricsAggregationDefinition[MinAggregationDefinition, MinAggregationBuilder] {
  val aggregationBuilder = AggregationBuilders.min(name)
}
