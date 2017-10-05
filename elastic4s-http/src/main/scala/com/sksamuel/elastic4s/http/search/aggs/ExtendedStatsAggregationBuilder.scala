package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.aggs.ExtendedStatsAggregationDefinition

object ExtendedStatsAggregationBuilder {
  def apply(agg: ExtendedStatsAggregationDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()

    builder.startObject("ext")
    agg.field.foreach(builder.field("field", _))
    agg.sigma.foreach(builder.field("sigma", _))
    agg.missing.foreach(builder.field("missing", _))

    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)

    builder
  }
}
