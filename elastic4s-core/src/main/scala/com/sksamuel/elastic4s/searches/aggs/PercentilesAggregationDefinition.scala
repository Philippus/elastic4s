package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.script.ScriptDefinition
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.percentiles.PercentilesAggregationBuilder

case class PercentilesAggregationDefinition(name: String)
  extends AggregationDefinition {

  type B = PercentilesAggregationBuilder
  val builder: B = AggregationBuilders.percentiles(name)

  def percents(percents: Double*): PercentilesAggregationDefinition = {
    builder.percentiles(percents: _*)
    this
  }

  def compression(compression: Double): PercentilesAggregationDefinition = {
    builder.compression(compression)
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
