package com.sksamuel.elastic4s.searches.queries.geo

import com.sksamuel.elastic4s.DistanceUnit
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.exts.OptionImplicits._

case class GeoDistanceQueryDefinition(field: String,
                                      geoDistance: Option[GeoDistance] = None,
                                      geohash: Option[String] = None,
                                      distanceStr: Option[String] = None,
                                      distance: Option[(Double, DistanceUnit)] = None,
                                      ignoreUnmapped: Option[Boolean] = None,
                                      boost: Option[Double] = None,
                                      validationMethod: Option[GeoValidationMethod] = None,
                                      queryName: Option[String] = None,
                                      point: Option[(Double, Double)] = None)
    extends QueryDefinition {

  def queryName(queryName: String): GeoDistanceQueryDefinition = copy(queryName = queryName.some)
  def boost(boost: Double): GeoDistanceQueryDefinition         = copy(boost = boost.some)

  // alias for geoDistance
  def distanceType(geod: GeoDistance): GeoDistanceQueryDefinition = geoDistance(geod)
  def geoDistance(geod: GeoDistance): GeoDistanceQueryDefinition  = copy(geoDistance = geod.some)

  def ignoreUnmapped(ignore: Boolean): GeoDistanceQueryDefinition = copy(ignoreUnmapped = ignore.some)

  def geohash(geohash: String): GeoDistanceQueryDefinition = copy(geohash = geohash.some)
  def validationMethod(validationMethod: GeoValidationMethod): GeoDistanceQueryDefinition =
    copy(validationMethod = validationMethod.some)

  def point(lat: Double, long: Double): GeoDistanceQueryDefinition = copy(point = (lat, long).some)

  def distance(distance: String): GeoDistanceQueryDefinition = copy(distanceStr = distance.some)
  def distance(distance: Double, unit: DistanceUnit): GeoDistanceQueryDefinition =
    copy(distance = (distance, unit).some)
}
