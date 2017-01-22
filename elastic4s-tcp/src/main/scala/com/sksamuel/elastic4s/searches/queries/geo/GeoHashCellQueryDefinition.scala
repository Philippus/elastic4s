package com.sksamuel.elastic4s.searches.queries.geo

import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import org.elasticsearch.common.geo.GeoHashUtils
import org.elasticsearch.index.query.QueryBuilders

case class GeoHashCellQueryDefinition(field: String,
                                      geohash: String,
                                      neighbors: Option[Boolean] = None,
                                      ignoreUnmapped: Option[Boolean] = None,
                                      precisionLevels: Option[Int] = None,
                                      precisionString: Option[String] = None,
                                      boost: Option[Double] = None,
                                      queryName: Option[String] = None) extends QueryDefinition {

  def builder = {
    val builder = QueryBuilders.geoHashCellQuery(field, geohash)
    precisionLevels.foreach(builder.precision)
    precisionString.foreach(builder.precision)
    neighbors.foreach(builder.neighbors)
    ignoreUnmapped.foreach(builder.ignoreUnmapped)
    boost.map(_.toFloat).foreach(builder.boost)
    queryName.foreach(builder.queryName)
    builder
  }

  def point(lat: Double, long: Double) :GeoHashCellQueryDefinition=
    copy(geohash = GeoHashUtils.stringEncode(lat, long))

  def withPrecision(precision: Int): GeoHashCellQueryDefinition = copy(precisionLevels = Some(precision))
  def withPrecision(precision: String): GeoHashCellQueryDefinition = copy(precisionString = Some(precision))
  def neighbours(neighbors: Boolean): GeoHashCellQueryDefinition = copy(neighbors = Some(neighbors))

  def ignoreUnmapped(ignoreUnmapped: Boolean): GeoHashCellQueryDefinition =
    copy(ignoreUnmapped = Option(ignoreUnmapped))

  def queryName(name: String): GeoHashCellQueryDefinition = copy(queryName = Option(name))
  def boost(boost: Double): GeoHashCellQueryDefinition = copy(boost = Option(boost))
}
