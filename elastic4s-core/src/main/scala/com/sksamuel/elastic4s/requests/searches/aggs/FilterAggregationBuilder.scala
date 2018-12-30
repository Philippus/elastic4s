package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.searches.queries.QueryBuilderFn
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object FilterAggregationBuilder {
  def apply(agg: FilterAggregation): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()

    builder.rawField("filter", QueryBuilderFn(agg.query))

    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)

    builder
  }
}
