package com.sksamuel.elastic4s.searches.queries

import org.elasticsearch.common.unit.DistanceUnit
import org.elasticsearch.index.query.{GeoDistanceQueryBuilder, QueryBuilders}

object GeoDistanceQueryBuilder {
  def apply(q: GeoDistanceQueryDefinition): GeoDistanceQueryBuilder = {
    val builder = QueryBuilders.geoDistanceQuery(q.field)
    q.geoDistance.map(_.name).map(org.elasticsearch.common.geo.GeoDistance.valueOf).foreach(builder.geoDistance)
    q.boost.map(_.toFloat).foreach(builder.boost)
    q.queryName.foreach(builder.queryName)
    q.geohash.foreach(builder.geohash)
    q.point.foreach { case (lat, long) => builder.point(lat, long) }
    q.distanceStr.foreach(builder.distance)
    q.distance.foreach { case (distance, unit) => builder.distance(distance, DistanceUnit.valueOf(unit.name)) }
    builder
  }
}
