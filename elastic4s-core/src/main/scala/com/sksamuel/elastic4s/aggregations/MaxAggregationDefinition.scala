package com.sksamuel.elastic4s.aggregations

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.max.MaxAggregationBuilder

case class MaxAggregationDefinition(name: String)
  extends ValuesSourceMetricsAggregationDefinition[MaxAggregationDefinition, MaxAggregationBuilder] {
  val aggregationBuilder = AggregationBuilders.max(name)
}
