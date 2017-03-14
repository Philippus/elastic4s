package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.searches.queries.geo.GeoPolygonQueryDefinition
import org.elasticsearch.common.geo.GeoPoint
import org.elasticsearch.index.query.{GeoPolygonQueryBuilder, QueryBuilders}

import scala.collection.JavaConverters._

object GeoPolygonQueryBuilderFn {
  def apply(q: GeoPolygonQueryDefinition): GeoPolygonQueryBuilder = {
    val builder = QueryBuilders.geoPolygonQuery(q.field, q.points.map(p => new GeoPoint(p.lat, p.getLon)).asJava)
    q.boost.foreach(builder.boost)
    q.queryName.foreach(builder.queryName)
    q.validationMethod.foreach(builder.setValidationMethod)
    q.ignoreUnmapped.foreach(builder.ignoreUnmapped)
    builder
  }
}
