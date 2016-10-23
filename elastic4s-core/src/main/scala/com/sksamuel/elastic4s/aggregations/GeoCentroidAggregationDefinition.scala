package com.sksamuel.elastic4s.aggregations

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.geocentroid.GeoCentroidAggregationBuilder

case class GeoCentroidAggregationDefinition(name: String)
  extends ValuesSourceMetricsAggregationDefinition[GeoCentroidAggregationDefinition, GeoCentroidAggregationBuilder] {

  val aggregationBuilder = AggregationBuilders.geoCentroid(name)

  def format(format: String): GeoCentroidAggregationDefinition = {
    aggregationBuilder.format(format)
    this
  }
}
