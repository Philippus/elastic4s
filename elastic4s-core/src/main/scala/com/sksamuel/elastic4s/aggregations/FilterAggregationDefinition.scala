package com.sksamuel.elastic4s.aggregations

import com.sksamuel.elastic4s.queries.QueryDefinition
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder

case class FilterAggregationDefinition(name: String, query: QueryDefinition)
  extends AggregationDefinition[FilterAggregationDefinition, FilterAggregationBuilder] {
  val aggregationBuilder = AggregationBuilders.filter(name, query.builder)
}
