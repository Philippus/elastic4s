package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.EnumConversions
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.{AggMetaDataFn, AutoDateHistogramAggregation, SubAggsBuilderFn}

object AutoDateHistogramAggregationBuilder {
  def apply(agg: AutoDateHistogramAggregation): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject("date_histogram")

    agg.timeZone.map(EnumConversions.timeZone).foreach(builder.field("time_zone", _))
    agg.minimumInterval.foreach(builder.field("minimum_interval", _))
    agg.buckets.foreach(builder.field("buckets", _))
    agg.format.foreach(builder.field("format", _))
    agg.field.foreach(builder.field("field", _))
    agg.missing.map(_.toString).foreach(builder.field("missing", _))
    builder.endObject()

    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
