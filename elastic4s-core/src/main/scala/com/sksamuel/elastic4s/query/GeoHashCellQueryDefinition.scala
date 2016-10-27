package com.sksamuel.elastic4s.query

import com.sksamuel.elastic4s.QueryDefinition
import org.elasticsearch.common.geo.GeoHashUtils
import org.elasticsearch.index.query.QueryBuilders

case class GeoHashCellQueryDefinition(field: String,
                                      geohash: String,
                                      neighbors: Option[Boolean] = None,
                                      precisionLevels: Option[Int] = None,
                                      precisionString: Option[String] = None) extends QueryDefinition {

  def builder = {
    val builder = QueryBuilders.geoHashCellQuery(field, geohash)
    precisionLevels.foreach(builder.precision)
    precisionString.foreach(builder.precision)
    neighbors.foreach(builder.neighbors)
    builder
  }

  def point(lat: Double, long: Double) = copy(geohash = GeoHashUtils.stringEncode(lat, long))
  def withPrecision(precision: Int): GeoHashCellQueryDefinition = copy(precisionLevels = Some(precision))
  def withPrecision(precision: String): GeoHashCellQueryDefinition = copy(precisionString = Some(precision))
  def neighbours(neighbors: Boolean) = copy(neighbors = Some(neighbors))
}
