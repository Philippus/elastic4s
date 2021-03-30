package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.handlers.script
import com.sksamuel.elastic4s.handlers.script.ScriptBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.{CompositeAggregation, DateHistogramValueSource, HistogramValueSource, SubAggsBuilderFn}

object CompositeAggregationBuilder {

  def apply(agg: CompositeAggregation): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject("composite")

    agg.size.foreach(builder.field("size", _))
    builder.startArray("sources")
    agg.sources.foreach(s => {
      builder.startObject()
      builder.startObject(s.name)
      builder.startObject(s.valueSourceType)
      s.field.foreach(builder.field("field", _))
      s.script.foreach(s => builder.rawField("script", script.ScriptBuilderFn(s)))
      s.order.foreach(builder.field("order", _))
      if (s.missingBucket) builder.field("missing_bucket", true)
      s match {
        case HistogramValueSource(_, interval, _, _, _, _) => builder.field("interval", interval)
        case DateHistogramValueSource(_, interval, _, _, _, timeZone, format, _) =>
          builder.field("interval", interval)
          timeZone.foreach(builder.field("time_zone", _))
          format.foreach(builder.field("format", _))
        case _ =>
      }
      builder.endObject()
      builder.endObject()
      builder.endObject()
    })
    builder.endArray()
    agg.after.map(afterMap => {
      builder.startObject("after")
      afterMap.map { case (k,v) => builder.autofield(k,v) }
      builder.endObject()
    })
    builder.endObject()

    SubAggsBuilderFn(agg, builder)

    builder
  }

}
