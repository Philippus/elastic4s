package com.sksamuel.elastic4s.requests.searches.queries.geo

import com.sksamuel.elastic4s.requests.common.DistanceUnit
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class GeoDistanceQuery(field: String,
                            geoDistance: Option[GeoDistance] = None,
                            geohash: Option[String] = None,
                            distanceStr: Option[String] = None,
                            distance: Option[(Double, DistanceUnit)] = None,
                            ignoreUnmapped: Option[Boolean] = None,
                            boost: Option[Double] = None,
                            validationMethod: Option[GeoValidationMethod] = None,
                            queryName: Option[String] = None,
                            point: Option[(Double, Double)] = None)
    extends Query {

  def queryName(queryName: String): GeoDistanceQuery = copy(queryName = queryName.some)
  def boost(boost: Double): GeoDistanceQuery         = copy(boost = boost.some)

  // alias for geoDistance
  def distanceType(geod: GeoDistance): GeoDistanceQuery = geoDistance(geod)
  def geoDistance(geod: GeoDistance): GeoDistanceQuery  = copy(geoDistance = geod.some)

  def ignoreUnmapped(ignore: Boolean): GeoDistanceQuery = copy(ignoreUnmapped = ignore.some)

  def geohash(geohash: String): GeoDistanceQuery = copy(geohash = geohash.some)
  def validationMethod(validationMethod: GeoValidationMethod): GeoDistanceQuery =
    copy(validationMethod = validationMethod.some)

  def point(lat: Double, long: Double): GeoDistanceQuery = copy(point = (lat, long).some)

  def distance(distance: String): GeoDistanceQuery = copy(distanceStr = distance.some)
  def distance(distance: Double, unit: DistanceUnit): GeoDistanceQuery =
    copy(distance = (distance, unit).some)
}
