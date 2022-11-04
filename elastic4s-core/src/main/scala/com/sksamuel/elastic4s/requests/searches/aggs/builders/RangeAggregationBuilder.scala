package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.handlers
import com.sksamuel.elastic4s.handlers.script.ScriptBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.{AbstractAggregation, AggMetaDataFn, RangeAggregation, SubAggsBuilderFn}

object RangeAggregationBuilder {
  def apply(agg: RangeAggregation, customAggregations: PartialFunction[AbstractAggregation, XContentBuilder]): XContentBuilder = {

    val builder = XContentFactory.obj.startObject("range")

    agg.field.foreach(builder.field("field", _))
    agg.missing.foreach(builder.autofield("missing", _))
    agg.format.foreach(builder.field("format", _))
    agg.keyed.foreach(builder.field("keyed", _))
    agg.script.foreach { script =>
      builder.rawField("script", handlers.script.ScriptBuilderFn(script))
    }

    builder.startArray("ranges")
    agg.unboundedTo.foreach {
      case (keyOpt, to) =>
        builder.startObject()
        keyOpt.foreach(builder.field("key", _))
        builder.field("to", to)
        builder.endObject()
    }
    agg.ranges.foreach {
      case (keyOpt, from, to) =>
        builder.startObject()
        keyOpt.foreach(builder.field("key", _))
        builder.field("from", from)
        builder.field("to", to)
        builder.endObject()
    }
    agg.unboundedFrom.foreach {
      case (keyOpt, from) =>
        builder.startObject()
        keyOpt.foreach(builder.field("key", _))
        builder.field("from", from)
        builder.endObject()
    }
    builder.endArray()

    builder.endObject()

    SubAggsBuilderFn(agg, builder, customAggregations)
    AggMetaDataFn(agg, builder)

    builder
  }
}
