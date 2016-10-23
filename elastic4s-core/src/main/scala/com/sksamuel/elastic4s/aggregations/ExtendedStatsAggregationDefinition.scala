package com.sksamuel.elastic4s.aggregations

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStatsAggregationBuilder

case class ExtendedStatsAggregationDefinition(name: String)
  extends ValuesSourceMetricsAggregationDefinition[ExtendedStatsAggregationDefinition, ExtendedStatsAggregationBuilder] {
  val aggregationBuilder = AggregationBuilders.extendedStats(name)
}
