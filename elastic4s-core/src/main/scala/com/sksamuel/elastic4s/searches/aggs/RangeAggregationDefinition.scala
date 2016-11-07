package com.sksamuel.elastic4s.searches.aggs

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.range.RangeAggregationBuilder

case class RangeAggregationDefinition(name: String) extends AggregationDefinition {

  type B = RangeAggregationBuilder
  val builder: B = AggregationBuilders.range(name)

  def range(from: Double, to: Double): RangeAggregationDefinition = {
    builder.addRange(from, to)
    this
  }

  def unboundedTo(to: Double): this.type = {
    builder.addUnboundedTo(to)
    this
  }

  def unboundedTo(key: String, to: Double): this.type = {
    builder.addUnboundedTo(key, to)
    this
  }

  def unboundedFrom(from: Double): this.type = {
    builder.addUnboundedFrom(from)
    this
  }

  def unboundedFrom(key: String, from: Double): this.type = {
    builder.addUnboundedFrom(key, from)
    this
  }

  def ranges(ranges: (Double, Double)*): this.type = {
    for ( range <- ranges )
      builder.addRange(range._1, range._2)
    this
  }

  def range(key: String, from: Double, to: Double): RangeAggregationDefinition = {
    builder.addRange(key, from, to)
    this
  }
}
