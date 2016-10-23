package com.sksamuel.elastic4s.sort

import org.elasticsearch.common.geo.GeoDistance
import org.elasticsearch.search.sort.{SortBuilders, SortOrder}

class GeoDistanceSortDefinition(field: String) extends SortDefinition {

  val builder = SortBuilders.geoDistanceSort(field)

  def missing(missing: AnyRef): this.type = {
    builder.missing(missing)
    this
  }

  def nested(nestedPath: String): this.type = {
    builder.setNestedPath(nestedPath)
    this
  }

  def mode(mode: MultiMode): this.type = {
    builder.sortMode(mode.elastic)
    this
  }

  def order(order: SortOrder): this.type = {
    builder.order(order)
    this
  }

  def geoDistance(geoDistance: GeoDistance): this.type = {
    builder.geoDistance(geoDistance)
    this
  }

  def geohash(geohash: String): this.type = geohashes(geohash)
  def geohashes(geohashes: String*): this.type = {
    builder.geohashes(geohashes: _*)
    this
  }

  def point(lat: Double, long: Double): this.type = {
    builder.point(lat, long)
    this
  }
}
