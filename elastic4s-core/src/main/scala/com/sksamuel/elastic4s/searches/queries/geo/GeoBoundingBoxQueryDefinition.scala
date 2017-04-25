package com.sksamuel.elastic4s.searches.queries.geo

import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import org.elasticsearch.common.geo.GeoPoint
import org.elasticsearch.index.query.{GeoExecType, GeoValidationMethod}
import com.sksamuel.exts.OptionImplicits._

case class Corners(top: Double, left: Double, bottom: Double, right: Double)

case class GeoBoundingBoxQueryDefinition(field: String,
                                         corners: Option[Corners] = None,
                                         geohash: Option[(String, String)] = None,
                                         cornersOGC: Option[(GeoPoint, GeoPoint)] = None,
                                         queryName: Option[String] = None,
                                         geoExecType: Option[GeoExecType] = None,
                                         validationMethod: Option[GeoValidationMethod] = None,
                                         ignoreUnmapped: Option[Boolean] = None)
  extends QueryDefinition {

  def geohash(topleft: String, bottomright: String): GeoBoundingBoxQueryDefinition = withGeohash(topleft, bottomright)
  def withGeohash(topleft: String, bottomright: String): GeoBoundingBoxQueryDefinition =
    copy(geohash = (topleft, bottomright).some)

  def corners(topLeft: GeoPoint, bottomRight: GeoPoint): GeoBoundingBoxQueryDefinition =
    corners(Corners(topLeft.lat, topLeft.lon, bottomRight.lat, bottomRight.lon))
  def corners(corners: Corners): GeoBoundingBoxQueryDefinition = copy(corners = corners.some)

  def withCorners(topLeft: GeoPoint, bottomRight: GeoPoint): GeoBoundingBoxQueryDefinition =
    corners(corners = Corners(topLeft.lat, topLeft.getLon, bottomRight.lat, bottomRight.getLon))
  def withCorners(corners: Corners): GeoBoundingBoxQueryDefinition = copy(corners = corners.some)
  def withCorners(top: Double, left: Double, bottom: Double, right: Double): GeoBoundingBoxQueryDefinition =
    withCorners(Corners(top, left, bottom, right))

  //  def withCornersOGC(bottomLeft: String, topRight: String): GeoBoundingBoxQueryDefinition =
  //    withCornersOGC(GeoPoint.fromGeohash(bottomLeft), GeoPoint.fromGeohash(topRight))

  def withCornersOGC(bottomLeft: GeoPoint, topRight: GeoPoint): GeoBoundingBoxQueryDefinition =
    copy(cornersOGC = Some(bottomLeft, topRight))

  def `type`(geoExecType: GeoExecType): GeoBoundingBoxQueryDefinition = withType(geoExecType)
  def withType(geoExecType: GeoExecType): GeoBoundingBoxQueryDefinition = copy(geoExecType = geoExecType.some)

  def ignoreUnmapped(ignore: Boolean): GeoBoundingBoxQueryDefinition = withIgnoreUnmapped(ignore)
  def withIgnoreUnmapped(ignore: Boolean): GeoBoundingBoxQueryDefinition = copy(ignoreUnmapped = ignore.some)

  def validationMethod(method: GeoValidationMethod): GeoBoundingBoxQueryDefinition = withValidationMethod(method)
  def withValidationMethod(method: GeoValidationMethod): GeoBoundingBoxQueryDefinition = copy(validationMethod = method.some)

  def queryName(queryName: String): GeoBoundingBoxQueryDefinition = withQueryName(queryName)
  def withQueryName(queryName: String): GeoBoundingBoxQueryDefinition = copy(queryName = queryName.some)
}
