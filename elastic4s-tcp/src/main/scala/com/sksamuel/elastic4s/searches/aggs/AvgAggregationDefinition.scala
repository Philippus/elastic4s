package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.ScriptBuilder
import com.sksamuel.elastic4s.script.ScriptDefinition
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder

case class AvgAggregationDefinition(name: String) extends AggregationDefinition {
  type B = AvgAggregationBuilder
  override val builder: B = AggregationBuilders.avg(name)

  def field(field: String) = {
    builder.field(field)
    this
  }

  def script(script: ScriptDefinition) = {
    builder.script(ScriptBuilder(script))
    this
  }
}
