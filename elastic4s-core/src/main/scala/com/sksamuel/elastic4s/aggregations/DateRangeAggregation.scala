package com.sksamuel.elastic4s.aggregations

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.range.date.DateRangeAggregationBuilder

case class DateRangeAggregation(name: String)
  extends ValuesSourceAggregationDefinition[DateRangeAggregation, DateRangeAggregationBuilder] {

  val aggregationBuilder = AggregationBuilders.dateRange(name)

  def range(from: String, to: String): DateRangeAggregation = {
    builder.addRange(from, to)
    this
  }

  def range(key: String, from: String, to: String): DateRangeAggregation = {
    builder.addRange(key, from, to)
    this
  }

  def range(from: Long, to: Long): DateRangeAggregation = {
    builder.addRange(from, to)
    this
  }

  def range(key: String, from: Long, to: Long): DateRangeAggregation = {
    builder.addRange(key, from, to)
    this
  }

  def unboundedFrom(from: String): DateRangeAggregation = {
    builder.addUnboundedFrom(from)
    this
  }

  def unboundedTo(to: String): DateRangeAggregation = {
    builder.addUnboundedTo(to)
    this
  }

  def unboundedFrom(key: String, from: String): DateRangeAggregation = {
    builder.addUnboundedFrom(key, from)
    this
  }

  def unboundedTo(key: String, to: String): DateRangeAggregation = {
    builder.addUnboundedTo(key, to)
    this
  }

  def format(fmt: String): DateRangeAggregation = {
    builder.format(fmt)
    this
  }
}
