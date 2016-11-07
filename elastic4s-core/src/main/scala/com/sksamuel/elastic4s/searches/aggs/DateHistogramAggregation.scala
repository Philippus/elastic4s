package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.script.ScriptDefinition
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.histogram.{DateHistogramAggregationBuilder, ExtendedBounds, Histogram}
import org.joda.time.DateTimeZone

case class DateHistogramAggregation(name: String) extends AggregationDefinition {

  type B = DateHistogramAggregationBuilder
  override val builder: B = AggregationBuilders.dateHistogram(name)

  def extendedBounds(bounds: ExtendedBounds): DateHistogramAggregation = {
    builder.extendedBounds(bounds)
    this
  }

  def interval(interval: Long): DateHistogramAggregation = {
    builder.interval(interval)
    this
  }

  def minDocCount(minDocCount: Long) = {
    builder.minDocCount(minDocCount)
    this
  }

  def timeZone(timeZone: DateTimeZone): this.type = {
    builder.timeZone(timeZone)
    this
  }

  def offset(offset: String) = {
    builder.offset(offset)
    this
  }

  def order(order: Histogram.Order) = {
    builder.order(order)
    this
  }

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
