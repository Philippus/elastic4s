package com.sksamuel.elastic4s.aggregations

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder

case class NestedAggregationDefinition(name: String, path: String)
  extends AggregationDefinition[NestedAggregationDefinition, NestedAggregationBuilder] {
  val aggregationBuilder = AggregationBuilders.nested(name, path)
}
