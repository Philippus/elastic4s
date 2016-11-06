package com.sksamuel.elastic4s.searches.sort

import org.elasticsearch.common.geo.{GeoDistance, GeoPoint}
import org.elasticsearch.search.sort.{GeoDistanceSortBuilder, SortBuilders, SortMode, SortOrder}

class GeoDistanceSortDefinition(field: String,
                                geohashes: Seq[String] = Nil,
                                points: Seq[GeoPoint] = Nil) extends SortDefinition[GeoDistanceSortBuilder] {

  val builder = SortBuilders.geoDistanceSort(field, geohashes: _*).points(points: _*)

  def nested(nestedPath: String): this.type = {
    builder.setNestedPath(nestedPath)
    this
  }

  def mode(mode: SortMode): this.type = {
    builder.sortMode(mode)
    this
  }

  def order(order: SortOrder): this.type = {
    builder.order(order)
    this
  }

  builder.geoDistance()

  def geoDistance(geoDistance: GeoDistance): this.type = {
    builder.geoDistance(geoDistance)
    this
  }
}
