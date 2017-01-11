package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.searches.queries.GeoPolygonQueryDefinition
import org.elasticsearch.common.geo.GeoPoint
import org.elasticsearch.index.query.{GeoPolygonQueryBuilder, GeoValidationMethod, QueryBuilders}

import scala.collection.JavaConverters._

object GeoPolygonQueryBuilder {
  def apply(q: GeoPolygonQueryDefinition): GeoPolygonQueryBuilder = {
    val builder = QueryBuilders.geoPolygonQuery(q.field, q.points.map(p => new GeoPoint(p.lat, p.long)).asJava)
    q.boost.foreach(builder.boost)
    q.queryName.foreach(builder.queryName)
    q.validationMethod.map(GeoValidationMethod.fromString).foreach(builder.setValidationMethod)
    q.ignoreUnmapped.foreach(builder.ignoreUnmapped)
    builder
  }
}
