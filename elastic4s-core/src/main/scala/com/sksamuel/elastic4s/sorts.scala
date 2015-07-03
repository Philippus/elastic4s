package com.sksamuel.elastic4s

import org.elasticsearch.search.sort.{ SortOrder, SortBuilder, SortBuilders }
import org.elasticsearch.common.geo.GeoDistance

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

class FieldSortDefinition(field: String) extends SortDefinition {

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

  def nestedFilter(nestedFilter: FilterDefinition) = {
    builder.setNestedFilter(nestedFilter.builder)
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

class ScriptSortDefinition(script: String) extends SortDefinition {

  def builder = {
    val b = SortBuilders
      .scriptSort(script, _type)
      .setNestedPath(_nestedPath)
      .lang(_lang)
      .order(_order)
      .sortMode(_sortmode)
    _params.foreach(pair => b.param(pair._1, pair._2))
    b
  }

  var _type = "string"
  var _missing: AnyRef = null
  var _nestedPath: String = null
  var _order: SortOrder = null
  var _sortmode: String = null
  var _lang: String = null
  var _params: Map[String, String] = Map.empty

  def sortMode(sortmode: String): ScriptSortDefinition = {
    _sortmode = sortmode
    this
  }

  def lang(lang: String): ScriptSortDefinition = {
    _lang = lang
    this
  }

  def as(`type`: String): ScriptSortDefinition = typed(`type`)
  def typed(`type`: String): ScriptSortDefinition = {
    _type = `type`
    this
  }

  def nestedPath(nestedPath: String): ScriptSortDefinition = {
    _nestedPath = nestedPath
    this
  }

  def order(order: SortOrder): ScriptSortDefinition = {
    _order = order
    this
  }

  def param(key: String, value: String): ScriptSortDefinition = {
    _params + (key -> value)
    this
  }

  def params(map: Map[String, String]): ScriptSortDefinition = {
    _params = map
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

class ScoreSortDefinition extends SortDefinition {

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
