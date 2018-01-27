package com.sksamuel.elastic4s.searches.queries.geo

import com.sksamuel.elastic4s.searches.GeoPoint
import com.sksamuel.exts.OptionImplicits._
import com.sksamuel.elastic4s.searches.queries.QueryDefinition

case class GeoHashCellQueryDefinition(field: String,
                                      geopoint: Option[GeoPoint] = None,
                                      geohash: Option[String] = None,
                                      neighbors: Option[Boolean] = None,
                                      ignoreUnmapped: Option[Boolean] = None,
                                      precisionLevels: Option[Int] = None,
                                      precisionString: Option[String] = None,
                                      boost: Option[Double] = None,
                                      queryName: Option[String] = None)
    extends QueryDefinition {

  def point(lat: Double, long: Double): GeoHashCellQueryDefinition = copy(geopoint = GeoPoint(lat, long).some)
  def point(geoPoint: GeoPoint): GeoHashCellQueryDefinition        = copy(geopoint = geoPoint.some)

  def geohash(geohash: String): GeoHashCellQueryDefinition = copy(geohash = geohash.some)

  def withPrecision(precision: Int): GeoHashCellQueryDefinition    = copy(precisionLevels = Some(precision))
  def withPrecision(precision: String): GeoHashCellQueryDefinition = copy(precisionString = Some(precision))
  def neighbours(neighbors: Boolean): GeoHashCellQueryDefinition   = copy(neighbors = Some(neighbors))

  def ignoreUnmapped(ignoreUnmapped: Boolean): GeoHashCellQueryDefinition =
    copy(ignoreUnmapped = Option(ignoreUnmapped))

  def queryName(name: String): GeoHashCellQueryDefinition = copy(queryName = Option(name))
  def boost(boost: Double): GeoHashCellQueryDefinition    = copy(boost = Option(boost))
}
