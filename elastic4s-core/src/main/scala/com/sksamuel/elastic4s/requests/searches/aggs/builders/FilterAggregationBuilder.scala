package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.handlers.searches.queries
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.{
  AbstractAggregation,
  AggMetaDataFn,
  FilterAggregation,
  SubAggsBuilderFn
}

object FilterAggregationBuilder {
  def apply(
      agg: FilterAggregation,
      customAggregations: PartialFunction[AbstractAggregation, XContentBuilder]
  ): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()

    builder.rawField("filter", queries.QueryBuilderFn(agg.query))

    SubAggsBuilderFn(agg, builder, customAggregations)
    AggMetaDataFn(agg, builder)

    builder
  }
}
