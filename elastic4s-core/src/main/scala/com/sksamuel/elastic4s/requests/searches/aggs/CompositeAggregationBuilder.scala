package com.sksamuel.elastic4s.requests.searches.aggs

import com.sksamuel.elastic4s.requests.script.ScriptBuilderFn
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

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
      s.script.foreach(s => builder.rawField("script", ScriptBuilderFn(s)))
      s.order.foreach(builder.field("order", _))
      s match {
        case HistogramValueSource(_, interval, _, _, _) => builder.field("interval", interval)
        case DateHistogramValueSource(_, interval, _, _, _, timeZone) => {
          builder.field("interval", interval)
          timeZone.foreach(builder.field("time_zone", _))
        }
        case _ =>
      }
      builder.endObject()
      builder.endObject()
      builder.endObject()
    })
    builder.endArray()
    builder.endObject()

    SubAggsBuilderFn(agg, builder)

    builder
  }

}
