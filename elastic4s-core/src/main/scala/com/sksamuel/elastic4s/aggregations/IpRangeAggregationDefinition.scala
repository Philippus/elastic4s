package com.sksamuel.elastic4s.aggregations

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.range.ip.IpRangeAggregationBuilder

case class IpRangeAggregationDefinition(name: String)
  extends ValuesSourceAggregationDefinition[IpRangeAggregationDefinition, IpRangeAggregationBuilder] {

  val aggregationBuilder = AggregationBuilders.ipRange(name)

  def maskRange(key: String, mask: String): this.type = {
    builder.addMaskRange(key, mask)
    this
  }

  def maskRange(mask: String): this.type = {
    builder.addMaskRange(mask)
    this
  }

  def range(from: String, to: String): this.type = {
    builder.addRange(from, to)
    this
  }

  def range(key: String, from: String, to: String): this.type = {
    builder.addRange(key, from, to)
    this
  }

  def unboundedFrom(from: String): this.type = {
    builder.addUnboundedFrom(from)
    this
  }

  def unboundedTo(to: String): this.type = {
    builder.addUnboundedTo(to)
    this
  }
}
