package com.sksamuel.elastic4s.aggregations

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.percentiles.PercentileRanksAggregationBuilder

case class PercentileRanksAggregationDefinition(name: String)
  extends ValuesSourceMetricsAggregationDefinition[PercentileRanksAggregationDefinition, PercentileRanksAggregationBuilder] {
  val aggregationBuilder = AggregationBuilders.percentileRanks(name)

  def percents(percents: Double*): PercentileRanksAggregationDefinition = {
    builder.percentiles(percents: _*)
    this
  }

  def compression(compression: Double): PercentileRanksAggregationDefinition = {
    builder.compression(compression)
    this
  }
}
