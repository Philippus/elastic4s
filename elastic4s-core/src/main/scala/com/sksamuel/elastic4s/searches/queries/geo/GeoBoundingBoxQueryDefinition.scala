package com.sksamuel.elastic4s.searches.queries.geo

import com.sksamuel.elastic4s.GeoPoint
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import org.elasticsearch.index.query.{GeoExecType, GeoValidationMethod}

case class GeoBoundingBoxQueryDefinition(field: String,
                                         corners: Option[(Double, Double, Double, Double)] = None,
                                         geohash: Option[String] = None,
                                         cornersOGC: Option[(GeoPoint, GeoPoint)] = None,
                                         queryName: Option[String] = None,
                                         geoExecType: Option[GeoExecType] = None,
                                         validationMethod: Option[GeoValidationMethod] = None,
                                         ignoreUnmapped: Option[Boolean] = None)
  extends QueryDefinition {

  def withGeohash(geohash: String): GeoBoundingBoxQueryDefinition = copy(geohash = Some(geohash))

  def withCorners(topLeft: GeoPoint, bottomRight: GeoPoint): GeoBoundingBoxQueryDefinition =
    copy(corners = Some(topLeft.lat, topLeft.long, bottomRight.lat, bottomRight.long))

  def withCorners(top: Double, left: Double, bottom: Double, right: Double): GeoBoundingBoxQueryDefinition =
    copy(corners = Some(top, left, bottom, right))

  //  def withCornersOGC(bottomLeft: String, topRight: String): GeoBoundingBoxQueryDefinition =
  //    withCornersOGC(GeoPoint.fromGeohash(bottomLeft), GeoPoint.fromGeohash(topRight))

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
