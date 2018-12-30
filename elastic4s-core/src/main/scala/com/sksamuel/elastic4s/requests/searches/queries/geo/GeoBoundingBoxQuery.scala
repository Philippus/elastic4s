package com.sksamuel.elastic4s.requests.searches.queries.geo

import com.sksamuel.elastic4s.requests.searches.GeoPoint
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class Corners(top: Double, left: Double, bottom: Double, right: Double)

case class GeoBoundingBoxQuery(field: String,
                               corners: Option[Corners] = None,
                               geohash: Option[(String, String)] = None,
                               cornersOGC: Option[(GeoPoint, GeoPoint)] = None,
                               queryName: Option[String] = None,
                               geoExecType: Option[GeoExecType] = None,
                               validationMethod: Option[GeoValidationMethod] = None,
                               ignoreUnmapped: Option[Boolean] = None)
    extends Query {

  def geohash(topleft: String, bottomright: String): GeoBoundingBoxQuery = withGeohash(topleft, bottomright)
  def withGeohash(topleft: String, bottomright: String): GeoBoundingBoxQuery =
    copy(geohash = (topleft, bottomright).some)

  def corners(topLeft: GeoPoint, bottomRight: GeoPoint): GeoBoundingBoxQuery =
    corners(Corners(topLeft.lat, topLeft.long, bottomRight.lat, bottomRight.long))
  def corners(corners: Corners): GeoBoundingBoxQuery = copy(corners = corners.some)

  def withCorners(topLeft: GeoPoint, bottomRight: GeoPoint): GeoBoundingBoxQuery =
    corners(corners = Corners(topLeft.lat, topLeft.long, bottomRight.lat, bottomRight.long))
  def withCorners(corners: Corners): GeoBoundingBoxQuery = copy(corners = corners.some)
  def withCorners(top: Double, left: Double, bottom: Double, right: Double): GeoBoundingBoxQuery =
    withCorners(Corners(top, left, bottom, right))

  //  def withCornersOGC(bottomLeft: String, topRight: String): GeoBoundingBoxQueryDefinition =
  //    withCornersOGC(GeoPoint.fromGeohash(bottomLeft), GeoPoint.fromGeohash(topRight))

  def withCornersOGC(bottomLeft: GeoPoint, topRight: GeoPoint): GeoBoundingBoxQuery =
    copy(cornersOGC = Some(bottomLeft, topRight))

  def `type`(geoExecType: GeoExecType): GeoBoundingBoxQuery   = withType(geoExecType)
  def withType(geoExecType: GeoExecType): GeoBoundingBoxQuery = copy(geoExecType = geoExecType.some)

  def ignoreUnmapped(ignore: Boolean): GeoBoundingBoxQuery     = withIgnoreUnmapped(ignore)
  def withIgnoreUnmapped(ignore: Boolean): GeoBoundingBoxQuery = copy(ignoreUnmapped = ignore.some)

  def validationMethod(method: GeoValidationMethod): GeoBoundingBoxQuery = withValidationMethod(method)
  def withValidationMethod(method: GeoValidationMethod): GeoBoundingBoxQuery =
    copy(validationMethod = method.some)

  def queryName(queryName: String): GeoBoundingBoxQuery     = withQueryName(queryName)
  def withQueryName(queryName: String): GeoBoundingBoxQuery = copy(queryName = queryName.some)
}
