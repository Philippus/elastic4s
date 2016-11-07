package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.script.ScriptDefinition
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.geocentroid.GeoCentroidAggregationBuilder

case class GeoCentroidAggregationDefinition(name: String) extends AggregationDefinition {

  type B = GeoCentroidAggregationBuilder
  val builder: B = AggregationBuilders.geoCentroid(name)

  def format(format: String): GeoCentroidAggregationDefinition = {
    builder.format(format)
    this
  }

  def field(field: String) = {
    builder.field(field)
    this
  }

  def script(script: ScriptDefinition) = {
    builder.script(script.build)
    this
  }

  def missing(missing: String) = {
    builder.missing(missing)
    this
  }
}
