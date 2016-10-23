package com.sksamuel.elastic4s.aggregations

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.children.ChildrenAggregationBuilder

case class ChildrenAggregationDefinition(name: String, childType: String)
  extends AggregationDefinition[ChildrenAggregationDefinition, ChildrenAggregationBuilder] {
  val aggregationBuilder = AggregationBuilders.children(name, childType)
}
