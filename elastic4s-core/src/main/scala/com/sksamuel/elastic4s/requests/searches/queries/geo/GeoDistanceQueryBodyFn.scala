package com.sksamuel.elastic4s.requests.searches.queries.geo

import com.sksamuel.elastic4s.{EnumConversions, XContentBuilder, XContentFactory}

object GeoDistanceQueryBodyFn {

  def apply(q: GeoDistanceQuery): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("geo_distance")
    q.distance.foreach {
      case (value, unit) => builder.field("distance", unit.toMeters(value) + "m")
    }
    q.distanceStr.foreach(builder.field("distance", _))
    q.point.foreach {
      // lat long is reversed in the builder
      case (lat, long) => builder.array(q.field, Array(long, lat))
    }
    q.geohash.foreach(builder.field(q.field, _))
    q.geoDistance.map(EnumConversions.geoDistance).foreach(builder.field("distance_type", _))
    q.ignoreUnmapped.foreach(builder.field("ignore_unmapped", _))
    q.validationMethod.map(EnumConversions.geoValidationMethod).foreach(builder.field("validation_method", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))
    builder.endObject()
    builder
  }
}
