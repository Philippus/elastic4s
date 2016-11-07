package com.sksamuel.elastic4s.searches.aggs

import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.global.GlobalAggregationBuilder

case class GlobalAggregationDefinition(name: String) extends AggregationDefinition {

  type B = GlobalAggregationBuilder
  override val builder: B = AggregationBuilders.global(name)
}
