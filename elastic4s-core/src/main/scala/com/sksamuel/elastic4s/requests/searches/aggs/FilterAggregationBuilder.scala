package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.queries.QueryBuilderFn

object FilterAggregationBuilder {
  def apply(agg: FilterAggregation): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()

    builder.rawField("filter", QueryBuilderFn(agg.query))

    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)

    builder
  }
}
