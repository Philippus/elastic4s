package com.sksamuel.elastic4s.aggregations

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder

case class ValueCountAggregationDefinition(name: String)
  extends ValuesSourceMetricsAggregationDefinition[ValueCountAggregationDefinition, ValueCountAggregationBuilder] {
  val aggregationBuilder = AggregationBuilders.count(name)
}
