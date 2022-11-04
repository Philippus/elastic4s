package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.EnumConversions
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.{AbstractAggregation, AggMetaDataFn, AutoDateHistogramAggregation, SubAggsBuilderFn}

object AutoDateHistogramAggregationBuilder {
  def apply(agg: AutoDateHistogramAggregation, customAggregations: PartialFunction[AbstractAggregation, XContentBuilder]): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject("auto_date_histogram")
    builder.field("field", agg.field)

    agg.timeZone.map(EnumConversions.timeZone).foreach(builder.field("time_zone", _))
    agg.minimumInterval.foreach(builder.field("minimum_interval", _))
    agg.buckets.foreach(builder.field("buckets", _))
    agg.format.foreach(builder.field("format", _))
    agg.missing.map(_.toString).foreach(builder.field("missing", _))
    builder.endObject()

    SubAggsBuilderFn(agg, builder, customAggregations)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
