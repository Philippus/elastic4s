package com.sksamuel.elastic4s.searches.aggs

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.children.ChildrenAggregationBuilder

case class ChildrenAggregationDefinition(name: String, childType: String) extends AggregationDefinition {
  type B = ChildrenAggregationBuilder
  override val builder: B = AggregationBuilders.children(name, childType)
}
