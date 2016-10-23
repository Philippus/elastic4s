package com.sksamuel.elastic4s.aggregations

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder

case class SumAggregationDefinition(name: String)
  extends ValuesSourceMetricsAggregationDefinition[SumAggregationDefinition, SumAggregationBuilder] {
  val aggregationBuilder = AggregationBuilders.sum(name)
}
