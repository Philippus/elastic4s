package com.sksamuel.elastic4s

import org.elasticsearch.common.geo.GeoDistance
import org.elasticsearch.search.sort.{SortBuilder, SortBuilders, SortOrder}

/** @author Stephen Samuel */
sealed abstract class MultiMode(val elastic: String)
case object MultiMode {
  case object Min extends MultiMode("min")
  case object Max extends MultiMode("max")
  case object Sum extends MultiMode("sum")
  case object Avg extends MultiMode("avg")
}

trait SortDefinition {
  def builder: SortBuilder
}

case class FieldSortDefinition(field: String) extends SortDefinition {

  val builder = SortBuilders.fieldSort(field)

  def missing(missing: AnyRef) = {
    builder.missing(missing)
    this
  }

  @deprecated("Use unmappedType", "1.4.0")
  def ignoreUnmapped(ignoreUnmapped: Boolean) = {
    builder.ignoreUnmapped(ignoreUnmapped)
    this
  }

  def unmappedType(`type`: String) = {
    builder.unmappedType(`type`)
    this
  }

  def nestedFilter(qb: QueryDefinition) = {
    builder.setNestedFilter(qb.builder)
    this
  }

  def nestedPath(nestedPath: String) = {
    builder.setNestedPath(nestedPath)
    this
  }

  def mode(mode: MultiMode) = {
    builder.sortMode(mode.elastic)
    this
  }

  def order(order: SortOrder) = {
    builder.order(order)
    this
  }
}

case class ScriptSortDefinition(script: ScriptDefinition, `type`: String) extends SortDefinition {

  val builder = SortBuilders.scriptSort(script.toJavaAPI, `type`)

  def sortMode(sortMode: String): this.type = {
    builder.sortMode(sortMode)
    this
  }

  def nestedPath(nestedPath: String): this.type = {
    builder.setNestedPath(nestedPath)
    this
  }

  def nestedFilter(nestedFilter: QueryDefinition): this.type = {
    builder.setNestedFilter(nestedFilter.builder)
    this
  }

  def order(order: SortOrder): this.type = {
    builder.order(order)
    this
  }

  def missing(missing: AnyRef): this.type = {
    builder.missing(missing)
    this
  }
}

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

case class ScoreSortDefinition() extends SortDefinition {

  val builder = SortBuilders.scoreSort()

  def missing(missing: AnyRef) = {
    builder.missing(missing)
    this
  }

  def order(order: SortOrder) = {
    builder.order(order)
    this
  }
}
