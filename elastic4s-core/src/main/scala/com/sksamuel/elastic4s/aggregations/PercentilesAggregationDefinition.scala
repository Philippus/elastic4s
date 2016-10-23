package com.sksamuel.elastic4s.aggregations

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.percentiles.PercentilesAggregationBuilder

case class PercentilesAggregationDefinition(name: String)
  extends ValuesSourceMetricsAggregationDefinition[PercentilesAggregationDefinition, PercentilesAggregationBuilder] {

  val aggregationBuilder = AggregationBuilders.percentiles(name)

  def percents(percents: Double*): PercentilesAggregationDefinition = {
    builder.percentiles(percents: _*)
    this
  }

  def compression(compression: Double): PercentilesAggregationDefinition = {
    builder.compression(compression)
    this
  }
}
