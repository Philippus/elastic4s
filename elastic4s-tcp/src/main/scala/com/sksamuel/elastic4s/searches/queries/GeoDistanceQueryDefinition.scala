package com.sksamuel.elastic4s.searches.queries

import org.elasticsearch.common.geo.GeoDistance
import org.elasticsearch.common.unit.DistanceUnit
import org.elasticsearch.common.unit.DistanceUnit.Distance
import org.elasticsearch.index.query.QueryBuilders

case class GeoDistanceQueryDefinition(field: String) extends QueryDefinition {

  val builder = QueryBuilders.geoDistanceQuery(field)
  val _builder = builder

  def geoDistance(geoDistance: GeoDistance): GeoDistanceQueryDefinition = {
    builder.geoDistance(geoDistance)
    this
  }

  def geohash(geohash: String): GeoDistanceQueryDefinition = {
    builder.geohash(geohash)
    this
  }

  def queryName(name: String): GeoDistanceQueryDefinition = {
    builder.queryName(name)
    this
  }

  def distance(distance: String): GeoDistanceQueryDefinition = {
    builder.distance(distance)
    this
  }

  def distance(distance: Double, unit: DistanceUnit): GeoDistanceQueryDefinition = {
    builder.distance(distance, unit)
    this
  }

  def distance(distance: Distance): GeoDistanceQueryDefinition = {
    builder.distance(distance.value, distance.unit)
    this
  }

  def point(lat: Double, long: Double): GeoDistanceQueryDefinition = {
    builder.point(lat, long)
    this
  }
}
