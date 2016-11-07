package com.sksamuel.elastic4s.searches.aggs

import org.elasticsearch.script.Script
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder

case class CardinalityAggregationDefinition(name: String) extends AggregationDefinition {

  type B = CardinalityAggregationBuilder
  val builder: B = AggregationBuilders.cardinality(name)

  def field(field: String) = {
    builder.field(field)
    this
  }

  def script(script: String) = {
    builder.script(new Script(script))
    this
  }

  @deprecated("no replacement - values will always be rehashed", "5.0.0")
  def rehash(rehash: Boolean) = {
    builder.rehash(rehash)
    this
  }

  def precisionThreshold(precisionThreshold: Long) = {
    builder.precisionThreshold(precisionThreshold)
    this
  }
}
