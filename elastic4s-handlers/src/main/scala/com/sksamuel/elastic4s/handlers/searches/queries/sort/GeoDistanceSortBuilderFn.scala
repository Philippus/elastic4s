package com.sksamuel.elastic4s.handlers.searches.queries.sort

import com.sksamuel.elastic4s.EnumConversions
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.sort.GeoDistanceSort

object GeoDistanceSortBuilderFn {
  def apply(geo: GeoDistanceSort): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("_geo_distance")

    if (geo.points.nonEmpty) {
      val point = geo.points.head
      builder.field(geo.field, s"${point.lat},${point.long}")
      builder.startArray(geo.field)
      geo.points.foreach { point =>
        builder.startArray()
        builder.value(point.long)
        builder.value(point.lat)
        builder.endArray()
      }
      builder.endArray()
    } else if (geo.geohashes.nonEmpty)
      builder.array(geo.field, geo.geohashes.toArray[String])

    geo.geoDistance.map(EnumConversions.geoDistance).foreach(builder.field("distance_type", _))
    geo.sortMode.map(EnumConversions.sortMode).foreach(builder.field("mode", _))
    geo.order.map(o => builder.field("order", EnumConversions.order(o)))
    geo.unit.map(EnumConversions.unit).foreach(unit => builder.field("unit", unit))

    geo.nested.foreach(n => builder.rawField("nested", NestedSortBuilderFn(n)))

    geo.ignoreUnmapped.foreach(builder.field("ignore_unmapped", _))

    builder
  }
}
