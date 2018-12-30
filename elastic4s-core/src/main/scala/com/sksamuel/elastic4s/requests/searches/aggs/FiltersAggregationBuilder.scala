package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.searches.queries.QueryBuilderFn
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object FiltersAggregationBuilder {
  def apply(agg: FiltersAggregation): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()

    val filters = {
      builder.startArray("filters")
      val filters = agg.filters.map(QueryBuilderFn.apply).map(_.string).mkString(",")
      builder.rawValue(filters)
      builder.endArray()
    }

    builder.rawField("filters", filters)

    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder
  }
}
