package com.sksamuel.elastic4s.search.query

import com.sksamuel.elastic4s.search.QueryDefinition
import org.elasticsearch.common.geo.GeoPoint
import org.elasticsearch.index.query.{GeoBoundingBoxQueryBuilder, GeoExecType, GeoValidationMethod, QueryBuilders}

case class GeoBoundingBoxQueryDefinition(field: String,
                                         corners: Option[(Double, Double, Double, Double)] = None,
                                         geohash: Option[String] = None,
                                         cornersOGC: Option[(GeoPoint, GeoPoint)] = None,
                                         queryName: Option[String] = None,
                                         geoExecType: Option[GeoExecType] = None,
                                         validationMethod: Option[GeoValidationMethod] = None,
                                         ignoreUnmapped: Option[Boolean] = None)
  extends QueryDefinition {

  def builder: GeoBoundingBoxQueryBuilder = {
    val builder = QueryBuilders.geoBoundingBoxQuery(field)
    corners.foreach { case (a, b, c, d) => builder.setCorners(a, b, c, d) }
    geohash.foreach(builder.setCorners)
    cornersOGC.foreach { case (bl, tr) => builder.setCornersOGC(bl, tr) }
    validationMethod.foreach(builder.setValidationMethod)
    ignoreUnmapped.foreach(builder.ignoreUnmapped)
    queryName.foreach(builder.queryName)
    geoExecType.foreach(builder.`type`)
    builder
  }

  def withGeohash(geohash: String): GeoBoundingBoxQueryDefinition = copy(geohash = Some(geohash))

  def withCorners(topLeft: GeoPoint, bottomRight: GeoPoint): GeoBoundingBoxQueryDefinition =
    copy(corners = Some(topLeft.getLat, topLeft.getLon, bottomRight.getLat, bottomRight.getLon))

  def withCorners(top: Double, left: Double, bottom: Double, right: Double): GeoBoundingBoxQueryDefinition =
    copy(corners = Some(top, left, bottom, right))

  def withCornersOGC(bottomLeft: String, topRight: String): GeoBoundingBoxQueryDefinition =
    withCornersOGC(GeoPoint.fromGeohash(bottomLeft), GeoPoint.fromGeohash(topRight))

  def withCornersOGC(bottomLeft: GeoPoint, topRight: GeoPoint): GeoBoundingBoxQueryDefinition =
    copy(cornersOGC = Some(bottomLeft, topRight))

  def withType(geoExecType: GeoExecType): GeoBoundingBoxQueryDefinition = copy(geoExecType = Option(geoExecType))

  def withIgnoreUnmapped(ignoreUnmapped: Boolean): GeoBoundingBoxQueryDefinition =
    copy(ignoreUnmapped = Option(ignoreUnmapped))

  def withValidationMethod(validationMethod: GeoValidationMethod): GeoBoundingBoxQueryDefinition =
    copy(validationMethod = Option(validationMethod))

  def withQueryName(queryName: String): GeoBoundingBoxQueryDefinition =
    copy(queryName = Option(queryName))
}
