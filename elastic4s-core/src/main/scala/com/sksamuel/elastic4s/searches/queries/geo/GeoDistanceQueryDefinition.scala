package com.sksamuel.elastic4s.searches.queries.geo

import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.common.geo.GeoDistance
import org.elasticsearch.common.unit.DistanceUnit

case class GeoDistanceQueryDefinition(field: String,
                                      geoDistance: Option[GeoDistance] = None,
                                      boost: Option[Double] = None,
                                      geohash: Option[String] = None,
                                      queryName: Option[String] = None,
                                      distanceStr: Option[String] = None,
                                      distance: Option[(Double, DistanceUnit)] = None,
                                      point: Option[(Double, Double)] = None
                                     ) extends QueryDefinition {

  def queryName(queryName: String): GeoDistanceQueryDefinition = copy(queryName = queryName.some)
  def boost(boost: Double): GeoDistanceQueryDefinition = copy(boost = boost.some)

  def geoDistance(geoDistance: GeoDistance): GeoDistanceQueryDefinition =
    copy(geoDistance = geoDistance.some)

  def geohash(geohash: String): GeoDistanceQueryDefinition = copy(geohash = geohash.some)

  def distance(distance: String): GeoDistanceQueryDefinition = copy(distanceStr = distance.some)

  def point(lat: Double, long: Double): GeoDistanceQueryDefinition = copy(point = (lat, long).some)

  def distance(distance: Double, unit: DistanceUnit): GeoDistanceQueryDefinition =
    copy(distance = (distance, unit).some)
}
