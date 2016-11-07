package com.sksamuel.elastic4s.searches.aggs

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder

case class NestedAggregationDefinition(name: String, path: String) extends AggregationDefinition {

  type B = NestedAggregationBuilder
  override val builder: B = AggregationBuilders.nested(name, path)
}

