package com.sksamuel.elastic4s.aggregations

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.stats.StatsAggregationBuilder

case class StatsAggregationDefinition(name: String)
  extends ValuesSourceMetricsAggregationDefinition[StatsAggregationDefinition, StatsAggregationBuilder] {
  val aggregationBuilder = AggregationBuilders.stats(name)
}
