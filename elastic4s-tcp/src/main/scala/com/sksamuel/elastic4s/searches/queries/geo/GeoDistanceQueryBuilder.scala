package com.sksamuel.elastic4s.searches.queries.geo

import com.sksamuel.elastic4s.EnumConversions
import org.elasticsearch.index.query.{GeoDistanceQueryBuilder, QueryBuilders}

object GeoDistanceQueryBuilder {

  import EnumConversions._

  def apply(q: GeoDistanceQueryDefinition): GeoDistanceQueryBuilder = {
    val builder = QueryBuilders.geoDistanceQuery(q.field)
    q.geoDistance.map(EnumConversions.geoDistance).foreach(builder.geoDistance)
    q.boost.map(_.toFloat).foreach(builder.boost)
    q.queryName.foreach(builder.queryName)
    q.geohash.foreach(builder.geohash)
    q.point.foreach { case (lat, long) => builder.point(lat, long) }
    q.distanceStr.foreach(builder.distance)
    q.distance.foreach { case (distance, unit) => builder.distance(distance, unit) }
    q.validationMethod.map(EnumConversions.geoValidationMethod).foreach(builder.setValidationMethod)
    builder
  }
}
