package com.sksamuel.elastic4s.searches.sort

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.elastic4s.searches.GeoPoint

sealed trait ScriptSortType
object ScriptSortType {
  def valueOf(str: String): ScriptSortType = str.toLowerCase match {
    case "string" => String
    case "number" => Number
  }
  case object String extends ScriptSortType
  case object Number extends ScriptSortType
}

trait SortApi {

  def scoreSort() = new ScoreSortExpectsOrder
  class ScoreSortExpectsOrder {
    def order(order: SortOrder): ScoreSortDefinition = ScoreSortDefinition(order)
  }

  def scoreSort(order: SortOrder): ScoreSortDefinition = ScoreSortDefinition(order)

  def scriptSort(script: ScriptDefinition): ScriptSortExpectsType = new ScriptSortExpectsType(script)
  class ScriptSortExpectsType(script: ScriptDefinition) {
    def typed(`type`: String): ScriptSortDefinition = typed(ScriptSortType.valueOf(`type`.toUpperCase))
    def typed(`type`: ScriptSortType): ScriptSortDefinition = ScriptSortDefinition(script, `type`)
  }

  def fieldSort(field: String) = FieldSortDefinition(field)

  def geoSort(field: String): GeoSortExpectsPoints = new GeoSortExpectsPoints(field)
  class GeoSortExpectsPoints(field: String) {
    def points(first: String, rest: String*): GeoDistanceSortDefinition = points(first +: rest)
    def points(geohashes: Seq[String]): GeoDistanceSortDefinition =
      GeoDistanceSortDefinition(field, geohashes, Nil)

    def points(first: GeoPoint, rest: GeoPoint*): GeoDistanceSortDefinition = points(first +: rest)
    def points(points: Iterable[GeoPoint]): GeoDistanceSortDefinition =
      GeoDistanceSortDefinition(field, Nil, points.toSeq)
  }
}
