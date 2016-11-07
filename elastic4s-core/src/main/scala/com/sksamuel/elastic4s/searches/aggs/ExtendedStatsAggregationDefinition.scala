package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.script.ScriptDefinition
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStatsAggregationBuilder

case class ExtendedStatsAggregationDefinition(name: String) extends AggregationDefinition {

  type B = ExtendedStatsAggregationBuilder
  val builder: B = AggregationBuilders.extendedStats(name)

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
