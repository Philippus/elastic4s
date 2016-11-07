package com.sksamuel.elastic4s.searches.aggs

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.missing.MissingAggregationBuilder

case class MissingAggregationDefinition(name: String) extends AggregationDefinition {

  type B = MissingAggregationBuilder
  override val builder: B = AggregationBuilders.missing(name)

  def field(field: String): this.type = {
    builder.field(field)
    this
  }
}
