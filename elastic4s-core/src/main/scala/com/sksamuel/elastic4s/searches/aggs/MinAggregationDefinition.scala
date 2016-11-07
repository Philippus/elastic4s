package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.script.ScriptDefinition
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.min.MinAggregationBuilder

case class MinAggregationDefinition(name: String) extends AggregationDefinition {

  type B = MinAggregationBuilder
  override val builder: B = AggregationBuilders.min(name)

  def format(format: String) = {
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
