package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.aggs.PercentilesAggregationDefinition

object PercentilesAggregationBuilder {
  def apply(agg: PercentilesAggregationDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject("percentiles")

    agg.field.foreach(builder.field("field", _))
    if (agg.percents.nonEmpty) {
      builder.array("percents", agg.percents.toArray)
    }

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

    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
