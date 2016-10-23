package com.sksamuel.elastic4s.aggregations

import org.elasticsearch.script.Script
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder

trait CardinalityMetricsAggregationDefinition[+Self <: CardinalityMetricsAggregationDefinition[Self]]
  extends MetricsAggregationDefinition[Self, CardinalityAggregationBuilder] {

  def field(field: String): CardinalityMetricsAggregationDefinition[Self] = {
    builder.field(field)
    this
  }

  def script(script: String): CardinalityMetricsAggregationDefinition[Self] = {
    builder.script(new Script(script))
    this
  }

  @deprecated("no replacement - values will always be rehashed", "3.0.0")
  def rehash(rehash: Boolean): CardinalityMetricsAggregationDefinition[Self] = {
    builder.rehash(rehash)
    this
  }

  def precisionThreshold(precisionThreshold: Long): CardinalityMetricsAggregationDefinition[Self] = {
    builder.precisionThreshold(precisionThreshold)
    this
  }
}
