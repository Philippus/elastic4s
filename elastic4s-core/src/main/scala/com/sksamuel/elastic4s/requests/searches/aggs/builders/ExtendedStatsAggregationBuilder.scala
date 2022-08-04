package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.script.ScriptBuilderFn
import com.sksamuel.elastic4s.requests.searches.aggs.{AggMetaDataFn, ExtendedStatsAggregation, SubAggsBuilderFn}

object ExtendedStatsAggregationBuilder {
  def apply(agg: ExtendedStatsAggregation): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()

    builder.startObject("extended_stats")
    agg.field.foreach(builder.field("field", _))
    agg.script.foreach { script =>
      builder.rawField("script", ScriptBuilderFn(script))
    }
    agg.sigma.foreach(builder.field("sigma", _))
    agg.missing.foreach(builder.autofield("missing", _))

    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)

    builder
  }
}
