package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.handlers
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.{
  AbstractAggregation,
  AggMetaDataFn,
  ExtendedStatsAggregation,
  SubAggsBuilderFn
}

object ExtendedStatsAggregationBuilder {
  def apply(
      agg: ExtendedStatsAggregation,
      customAggregations: PartialFunction[AbstractAggregation, XContentBuilder]
  ): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()

    builder.startObject("extended_stats")
    agg.field.foreach(builder.field("field", _))
    agg.script.foreach { script =>
      builder.rawField("script", handlers.script.ScriptBuilderFn(script))
    }
    agg.sigma.foreach(builder.field("sigma", _))
    agg.missing.foreach(builder.autofield("missing", _))

    SubAggsBuilderFn(agg, builder, customAggregations)
    AggMetaDataFn(agg, builder)

    builder
  }
}
