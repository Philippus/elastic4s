package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.ScriptBuilder
import com.sksamuel.elastic4s.script.ScriptDefinition
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.range.ip.IpRangeAggregationBuilder

case class IpRangeAggregationDefinition(name: String) extends AggregationDefinition {

  type B = IpRangeAggregationBuilder
  override val builder: B = AggregationBuilders.ipRange(name)

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
}
