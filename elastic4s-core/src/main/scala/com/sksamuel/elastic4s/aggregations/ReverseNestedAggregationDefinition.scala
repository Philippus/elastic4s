package com.sksamuel.elastic4s.aggregations

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.nested.ReverseNestedAggregationBuilder

case class ReverseNestedAggregationDefinition(name: String)
  extends AggregationDefinition[ReverseNestedAggregationDefinition, ReverseNestedAggregationBuilder] {
  val aggregationBuilder = AggregationBuilders.reverseNested(name)

  def path(path: String): ReverseNestedAggregationDefinition = {
    builder.path(path)
    this
  }
}
