package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.ScriptBuilder
import com.sksamuel.elastic4s.script.ScriptDefinition
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.stats.StatsAggregationBuilder

case class StatsAggregationDefinition(name: String) extends AggregationDefinition {

  type B = StatsAggregationBuilder
  val builder: B = AggregationBuilders.stats(name)

  def field(field: String) = {
    builder.field(field)
    this
  }

  def format(format: String) = {
    builder.format(format)
    this
  }

  def script(script: ScriptDefinition) = {
    builder.script(ScriptBuilder(script))
    this
  }

  def missing(missing: String) = {
    builder.missing(missing)
    this
  }
}
