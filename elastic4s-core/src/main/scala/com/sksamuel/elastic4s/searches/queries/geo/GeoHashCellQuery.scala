package com.sksamuel.elastic4s.searches.queries.geo

import com.sksamuel.elastic4s.searches.GeoPoint
import com.sksamuel.exts.OptionImplicits._
import com.sksamuel.elastic4s.searches.queries.Query

case class GeoHashCellQuery(field: String,
                            geopoint: Option[GeoPoint] = None,
                            geohash: Option[String] = None,
                            neighbors: Option[Boolean] = None,
                            ignoreUnmapped: Option[Boolean] = None,
                            precisionLevels: Option[Int] = None,
                            precisionString: Option[String] = None,
                            boost: Option[Double] = None,
                            queryName: Option[String] = None)
    extends Query {

  def point(lat: Double, long: Double): GeoHashCellQuery = copy(geopoint = GeoPoint(lat, long).some)
  def point(geoPoint: GeoPoint): GeoHashCellQuery        = copy(geopoint = geoPoint.some)

  def geohash(geohash: String): GeoHashCellQuery = copy(geohash = geohash.some)

  def withPrecision(precision: Int): GeoHashCellQuery    = copy(precisionLevels = Some(precision))
  def withPrecision(precision: String): GeoHashCellQuery = copy(precisionString = Some(precision))
  def neighbours(neighbors: Boolean): GeoHashCellQuery   = copy(neighbors = Some(neighbors))

  def ignoreUnmapped(ignoreUnmapped: Boolean): GeoHashCellQuery =
    copy(ignoreUnmapped = Option(ignoreUnmapped))

  def queryName(name: String): GeoHashCellQuery = copy(queryName = Option(name))
  def boost(boost: Double): GeoHashCellQuery    = copy(boost = Option(boost))
}
