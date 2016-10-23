package com.sksamuel.elastic4s.aggregations

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.global.GlobalAggregationBuilder

case class GlobalAggregationDefinition(name: String)
  extends AggregationDefinition[GlobalAggregationDefinition, GlobalAggregationBuilder] {
  val aggregationBuilder = AggregationBuilders.global(name)
}
