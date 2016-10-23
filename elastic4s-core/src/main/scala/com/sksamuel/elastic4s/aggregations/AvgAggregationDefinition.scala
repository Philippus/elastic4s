package com.sksamuel.elastic4s.aggregations

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder

case class AvgAggregationDefinition(name: String)
  extends ValuesSourceMetricsAggregationDefinition[AvgAggregationDefinition, AvgAggregationBuilder] {
  val aggregationBuilder = AggregationBuilders.avg(name)
}
