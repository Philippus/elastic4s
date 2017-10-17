package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.http.search.queries.QueryBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.aggs.KeyedFiltersAggregationDefinition

object KeyedFiltersAggregationBuilder {
  def apply(agg: KeyedFiltersAggregationDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()

    val filters = {
      builder.startObject("filters")
      agg.filters.map(map => {
        builder.rawField(map._1, QueryBuilderFn(map._2))
      })
      builder.endObject()
    }

    builder.rawField("filters", filters)

    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder
  }
}

