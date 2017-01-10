package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.ScriptBuilder
import com.sksamuel.elastic4s.script.ScriptDefinition
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.max.MaxAggregationBuilder

case class MaxAggregationDefinition(name: String) extends AggregationDefinition {

  type B = MaxAggregationBuilder
  override val builder: B = AggregationBuilders.max(name)

  def format(format: String) = {
    builder.format(format)
    this
  }

  def field(field: String) = {
    builder.field(field)
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
