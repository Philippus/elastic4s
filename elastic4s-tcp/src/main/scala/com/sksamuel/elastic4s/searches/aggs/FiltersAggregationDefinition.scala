package com.sksamuel.elastic4s.searches.aggs

import com.sksamuel.elastic4s.searches.QueryBuilderFn
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.filters.FiltersAggregationBuilder
import org.elasticsearch.search.aggregations.bucket.filters.FiltersAggregator.KeyedFilter

case class FiltersAggregationDefinition(name: String, filters: Iterable[QueryDefinition])
  extends AggregationDefinition {
  type B = FiltersAggregationBuilder
  val builder: B = AggregationBuilders.filters(name, filters.map(QueryBuilderFn.apply).toSeq: _*)
}

case class KeyedFiltersAggregationDefinition(name: String, filters: Iterable[(String, QueryDefinition)])
  extends AggregationDefinition {
  type B = FiltersAggregationBuilder
  val builder: B = AggregationBuilders.filters(name,
    filters.map {
      case (name, query) => new KeyedFilter(name, QueryBuilderFn(query))
    }
      .toSeq: _*)
}
