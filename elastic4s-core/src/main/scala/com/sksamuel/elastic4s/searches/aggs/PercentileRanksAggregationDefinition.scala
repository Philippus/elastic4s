package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.script.ScriptDefinition
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.percentiles.{PercentileRanksAggregationBuilder, PercentilesMethod}

case class PercentileRanksAggregationDefinition(name: String) extends AggregationDefinition {

  type B = PercentileRanksAggregationBuilder
  override val builder: B = AggregationBuilders.percentileRanks(name)

  def keyed(keyed: Boolean): PercentileRanksAggregationDefinition = {
    builder.keyed(keyed)
    this
  }

  def values(values: Double*): PercentileRanksAggregationDefinition = {
    builder.values(values: _*)
    this
  }

  def method(method: PercentilesMethod): PercentileRanksAggregationDefinition = {
    builder.method(method)
    this
  }

  def compression(compression: Double): PercentileRanksAggregationDefinition = {
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
