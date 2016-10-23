package com.sksamuel.elastic4s.queries

import org.elasticsearch.common.geo.GeoPoint
import org.elasticsearch.index.query.QueryBuilders

case class GeoPolygonQueryDefinition(field: String,
                                     points: Seq[GeoPoint],
                                     ignoreUnmapped: Option[Boolean] = None)
  extends QueryDefinition {

  import scala.collection.JavaConverters._

  def builder: org.elasticsearch.index.query.QueryBuilder = {
    val builder = QueryBuilders.geoPolygonQuery(field, points.asJava)
    ignoreUnmapped.foreach(builder.ignoreUnmapped)
    builder.setValidationMethod()
    builder.boost()
    builder.queryName()
    builder.rewrite()
    builder.toQuery()
    builder.toFilter()
    builder.
      builder
  }

  def ignoreUnmapped(ignore: Boolean): GeoPolygonQueryDefinition = copy(ignoreUnmapped = Some(ignore))
  def ignoreUnmapped(ignore: Boolean): GeoPolygonQueryDefinition = copy(ignoreUnmapped = Some(ignore))

  def point(geohash: String): GeoPolygonQueryDefinition = {
    builder.addPoint(geohash)
    this
  }

  def point(lat: Double, lon: Double): GeoPolygonQueryDefinition = {
    builder.addPoint(lat, lon)
    this
  }

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}
