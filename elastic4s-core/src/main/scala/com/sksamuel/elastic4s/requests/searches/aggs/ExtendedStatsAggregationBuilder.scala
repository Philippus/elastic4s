package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

object ExtendedStatsAggregationBuilder {
  def apply(agg: ExtendedStatsAggregation): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()

    builder.startObject("extended_stats")
    agg.field.foreach(builder.field("field", _))
    agg.sigma.foreach(builder.field("sigma", _))
    agg.missing.foreach(builder.field("missing", _))

    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)

    builder
  }
}
