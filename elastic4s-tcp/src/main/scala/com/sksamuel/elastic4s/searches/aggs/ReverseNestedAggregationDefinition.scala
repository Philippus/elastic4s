package com.sksamuel.elastic4s.searches.aggs

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.nested.ReverseNestedAggregationBuilder

case class ReverseNestedAggregationDefinition(name: String) extends AggregationDefinition {

  type B = ReverseNestedAggregationBuilder
  val builder: B = AggregationBuilders.reverseNested(name)

  def path(path: String): ReverseNestedAggregationDefinition = {
    builder.path(path)
    this
  }
}
