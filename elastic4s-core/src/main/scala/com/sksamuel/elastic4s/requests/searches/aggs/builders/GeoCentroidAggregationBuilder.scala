package com.sksamuel.elastic4s.requests.searches.aggs.builders

import com.sksamuel.elastic4s.handlers
import com.sksamuel.elastic4s.handlers.script.ScriptBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.aggs.{AggMetaDataFn, GeoCentroidAggregation}

object GeoCentroidAggregationBuilder {
  def apply(agg: GeoCentroidAggregation): XContentBuilder = {

    val builder = XContentFactory.obj.startObject("geo_centroid")

    agg.field.foreach(builder.field("field", _))
    agg.format.foreach(builder.field("format", _))
    agg.missing.foreach(builder.autofield("missing", _))
    agg.script.foreach { script =>
      builder.rawField("script", handlers.script.ScriptBuilderFn(script))
    }

    builder.endObject()

    AggMetaDataFn(agg, builder)
    builder.endObject()
  }
}
