package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.handlers.searches.queries
import com.sksamuel.elastic4s.handlers.searches.queries.QueryBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.{AggMetaDataFn, FilterAggregation, SubAggsBuilderFn}

object FilterAggregationBuilder {
  def apply(agg: FilterAggregation): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()

    builder.rawField("filter", queries.QueryBuilderFn(agg.query))

    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)

    builder
  }
}
