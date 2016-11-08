package com.sksamuel.elastic4s.searches.sort

import com.sksamuel.elastic4s.searches.QueryDefinition
import org.elasticsearch.common.geo.{GeoDistance, GeoPoint}
import org.elasticsearch.common.unit.DistanceUnit
import org.elasticsearch.index.query.GeoValidationMethod
import org.elasticsearch.search.sort.{GeoDistanceSortBuilder, SortBuilders, SortMode, SortOrder}

class GeoDistanceSortDefinition(field: String,
                                geohashes: Seq[String] = Nil,
                                points: Seq[GeoPoint] = Nil) extends SortDefinition[GeoDistanceSortBuilder] {

  val builder = SortBuilders.geoDistanceSort(field, geohashes: _*).points(points: _*)

  def nested(nestedPath: String): this.type = {
    builder.setNestedPath(nestedPath)
    this
  }

  @deprecated("use sortMode", "5.0.0")
  def mode(mode: SortMode): this.type = sortMode(mode)
  def sortMode(mode: SortMode): this.type = {
    builder.sortMode(mode)
    this
  }


  def order(order: SortOrder): this.type = {
    builder.order(order)
    this
  }

  def validation(validation: GeoValidationMethod): this.type = {
    builder.validation(validation)
    this
  }

  def unit(unit: DistanceUnit): this.type = {
    builder.unit(unit)
    this
  }

  def nestedPath(nestedPath: String): this.type = {
    builder.setNestedPath(nestedPath)
    this
  }

  def nestedFilter(filter: QueryDefinition): this.type = {
    builder.setNestedFilter(filter.builder)
    this
  }

  def geoDistance(geoDistance: GeoDistance): this.type = {
    builder.geoDistance(geoDistance)
    this
  }
}
