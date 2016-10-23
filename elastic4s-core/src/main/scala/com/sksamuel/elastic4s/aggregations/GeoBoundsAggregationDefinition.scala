package com.sksamuel.elastic4s.aggregations

import org.elasticsearch.script.Script
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.geobounds.GeoBoundsAggregationBuilder

case class GeoBoundsAggregationDefinition(name: String)
  extends ValuesSourceAggregationDefinition[GeoBoundsAggregationDefinition, GeoBoundsAggregationBuilder] {

  val aggregationBuilder = AggregationBuilders.geoBounds(name)

  def script(script: Script): GeoBoundsAggregationDefinition = {
    aggregationBuilder.script(script)
    this
  }

  def wrapLongitude(wrapLongitude: Boolean): GeoBoundsAggregationDefinition = {
    builder.wrapLongitude(wrapLongitude)
    this
  }
}
