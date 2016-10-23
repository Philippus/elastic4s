package com.sksamuel.elastic4s.aggregations

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.histogram.{DateHistogramAggregationBuilder, DateHistogramInterval, ExtendedBounds, Histogram}
import org.joda.time.DateTimeZone

case class DateHistogramAggregation(name: String)
  extends ValuesSourceAggregationDefinition[DateHistogramAggregation, DateHistogramAggregationBuilder] {
  val aggregationBuilder = AggregationBuilders.dateHistogram(name)

  def extendedBounds(minMax: (String, String)): DateHistogramAggregation = {
    builder.extendedBounds(new ExtendedBounds(minMax._1, minMax._2))
    this
  }

  def extendedBounds(bounds: ExtendedBounds): DateHistogramAggregation = {
    builder.extendedBounds(bounds)
    this
  }

  def interval(interval: Long): DateHistogramAggregation = {
    builder.interval(interval)
    this
  }

  def interval(interval: DateHistogramInterval): DateHistogramAggregation = {
    builder.dateHistogramInterval(interval)
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
}
