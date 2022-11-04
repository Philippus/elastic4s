package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.handlers.script.ScriptBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.{AbstractAggregation, AggMetaDataFn, PercentilesAggregation, SubAggsBuilderFn}

object PercentilesAggregationBuilder {
  def apply(agg: PercentilesAggregation, customAggregations: PartialFunction[AbstractAggregation, XContentBuilder]): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject("percentiles")

    agg.field.foreach(builder.field("field", _))
    agg.keyed.foreach(builder.field("keyed", _))

    if (agg.percents.nonEmpty)
      builder.array("percents", agg.percents.toArray)

    agg.script.map(ScriptBuilderFn.apply).foreach(builder.rawField("script", _))

    agg.compression.foreach { compression =>
      builder.startObject("tdigest")
      builder.field("compression", compression)
      builder.endObject()
    }

    agg.numberOfSignificantValueDigits.foreach { sig =>
      builder.startObject("hdr")
      builder.field("number_of_significant_value_digits", sig)
      builder.endObject()
    }

    agg.missing.map(_.toString).foreach(builder.field("missing", _))

    SubAggsBuilderFn(agg, builder, customAggregations)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
