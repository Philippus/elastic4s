package com.sksamuel.elastic4s.queries

import org.elasticsearch.index.query.QueryBuilders

case class GeoHashCellQuery(field: String)
  extends QueryDefinition {

  val builder = QueryBuilders.geoHashCellQuery(field)
  val _builder = builder

  def point(lat: Double, long: Double): this.type = {
    builder.point(lat, long)
    this
  }

  def geohash(geohash: String): this.type = {
    builder.geohash(geohash)
    this
  }

  def neighbours(neighbours: Boolean): this.type = {
    builder.neighbors(neighbours)
    this
  }
}
