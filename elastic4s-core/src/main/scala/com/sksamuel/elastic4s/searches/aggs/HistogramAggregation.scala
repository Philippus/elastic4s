package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.script.ScriptDefinition
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.histogram.{Histogram, HistogramAggregationBuilder}

case class HistogramAggregation(name: String) extends AggregationDefinition {

  type B = HistogramAggregationBuilder
  override val builder: B = AggregationBuilders.histogram(name)

  def interval(interval: Long): HistogramAggregation = {
    builder.interval(interval)
    this
  }

  def minDocCount(minDocCount: Long): HistogramAggregation = {
    builder.minDocCount(minDocCount)
    this
  }

  def order(order: Histogram.Order) = {
    builder.order(order)
    this
  }

  def offset(offset: Long): HistogramAggregation = {
    builder.offset(offset)
    this
  }

  def extendedBounds(min: Long, max: Long): HistogramAggregation = {
    builder.extendedBounds(min, max)
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
