package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.searches.QueryDefinition
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.filters.FiltersAggregationBuilder

case class FiltersAggregationDefinition(name: String, filters: Iterable[QueryDefinition])
  extends AggregationDefinition {
  type B = FiltersAggregationBuilder
  val builder: B = AggregationBuilders.filters(name, filters.map(_.builder).toSeq: _*)
}
