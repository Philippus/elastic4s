package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.searches.QueryDefinition
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder

case class FilterAggregationDefinition(name: String, query: QueryDefinition)
  extends AggregationDefinition {

  type B = FilterAggregationBuilder
  val builder: B = AggregationBuilders.filter(name, query.builder)
}
