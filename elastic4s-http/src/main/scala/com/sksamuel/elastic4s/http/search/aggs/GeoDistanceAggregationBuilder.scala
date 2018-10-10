package com.sksamuel.elastic4s.http.search.aggs

import com.sksamuel.elastic4s.http.EnumConversions
import com.sksamuel.elastic4s.http.ScriptBuilderFn
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.aggs.GeoDistanceAggregation

object GeoDistanceAggregationBuilder {
  def apply(agg: GeoDistanceAggregation): XContentBuilder = {

    val builder = XContentFactory.obj()

    builder.startObject("geo_distance")

    builder.startObject("origin")
    builder.field("lat", agg.origin.lat)
    builder.field("lon", agg.origin.long)
    builder.endObject()

    agg.field.foreach(builder.field("field", _))
    agg.format.foreach(builder.field("format", _))
    agg.missing.foreach(builder.autofield("missing", _))
    agg.keyed.foreach(builder.field("keyed", _))

    agg.distanceType.map(EnumConversions.geoDistance).foreach(builder.field("distance_type", _))
    agg.unit.map(EnumConversions.unit).foreach(builder.field("unit", _))

    agg.script.foreach { script =>
      builder.rawField("script", ScriptBuilderFn(script))
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

    SubAggsBuilderFn(agg, builder)
    AggMetaDataFn(agg, builder)

    builder
  }
}
