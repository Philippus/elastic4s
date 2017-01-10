package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.ScriptBuilder
import com.sksamuel.elastic4s.script.ScriptDefinition
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.range.date.DateRangeAggregationBuilder

case class DateRangeAggregation(name: String)
  extends AggregationDefinition {

  type B = DateRangeAggregationBuilder
  val builder: B = AggregationBuilders.dateRange(name)

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
