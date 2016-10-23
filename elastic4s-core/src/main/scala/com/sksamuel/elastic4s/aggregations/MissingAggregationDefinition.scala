package com.sksamuel.elastic4s.aggregations

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.missing.MissingAggregationBuilder

case class MissingAggregationDefinition(name: String)
  extends AggregationDefinition[MissingAggregationDefinition, MissingAggregationBuilder] {
  val aggregationBuilder = AggregationBuilders.missing(name)

  def field(field: String): this.type = {
    builder.field(field)
    this
  }
}
