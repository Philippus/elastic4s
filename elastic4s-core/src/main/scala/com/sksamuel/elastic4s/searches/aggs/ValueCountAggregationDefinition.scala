package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.script.ScriptDefinition
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder

case class ValueCountAggregationDefinition(name: String) extends AggregationDefinition {

  type B = ValueCountAggregationBuilder
  val builder: B = AggregationBuilders.count(name)

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
