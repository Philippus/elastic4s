package com.sksamuel.elastic4s.searches.aggs

import org.elasticsearch.script.Script
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.range.date.DateRangeAggregationBuilder

case class GeoBoundsAggregationDefinition(name: String)
  extends AggregationDefinition {

  type B = DateRangeAggregationBuilder
  val builder: B = AggregationBuilders.dateRange(name)

  val aggregationBuilder = AggregationBuilders.geoBounds(name)

  def script(script: Script): GeoBoundsAggregationDefinition = {
    aggregationBuilder.script(script)
    this
  }

  def addRange(from: Double, to: Double): GeoBoundsAggregationDefinition = {
    builder.addRange(from, to)
    this
  }

  def addUnboundedTo(to: Double): GeoBoundsAggregationDefinition = {
    builder.addUnboundedTo(to)
    this
  }

  def addUnboundedFrom(from: Double): GeoBoundsAggregationDefinition = {
    builder.addUnboundedFrom(from)
    this
  }
}
