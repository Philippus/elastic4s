package com.sksamuel.elastic4s.aggregations

import com.sksamuel.elastic4s.ScriptDefinition
import org.elasticsearch.search.aggregations.support.ValuesSourceAggregationBuilder

trait ValuesSourceAggregationDefinition[+Self <: ValuesSourceAggregationDefinition[Self, B], B <: ValuesSourceAggregationBuilder[B]]
  extends AggregationDefinition[Self, B] {
  self: Self =>

  def field(field: String): Self = {
    builder.field(field)
    this
  }

  def script(script: ScriptDefinition): Self = {
    builder.script(script.toJavaAPI)
    this
  }

  def missing(missing: String): Self = {
    aggregationBuilder.missing(missing)
    this
  }
}
