package com.sksamuel.elastic4s.requests.searches.sort

import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.elastic4s.requests.searches.GeoPoint

sealed trait ScriptSortType
object ScriptSortType {
  def valueOf(str: String): ScriptSortType = str.toLowerCase match {
    case "string" => String
    case "number" => Number
  }
  case object String extends ScriptSortType
  case object Number extends ScriptSortType

  def STRING = String
  def NUMBER = Number
}

trait SortApi {

  def scoreSort() = new ScoreSortExpectsOrder
  class ScoreSortExpectsOrder {
    def order(order: SortOrder): ScoreSort = ScoreSort(order)
  }

  def scoreSort(order: SortOrder): ScoreSort = ScoreSort(order)

  def scriptSort(script: Script): ScriptSortExpectsType = new ScriptSortExpectsType(script)
  class ScriptSortExpectsType(script: Script) {
    def typed(`type`: String): ScriptSort         = typed(ScriptSortType.valueOf(`type`.toUpperCase))
    def typed(`type`: ScriptSortType): ScriptSort = ScriptSort(script, `type`)
  }

  def fieldSort(field: String) = FieldSort(field)

  def geoSort(field: String): GeoSortExpectsPoints = new GeoSortExpectsPoints(field)
  class GeoSortExpectsPoints(field: String) {
    def points(first: String, rest: String*): GeoDistanceSort = points(first +: rest)
    def points(geohashes: Seq[String]): GeoDistanceSort =
      GeoDistanceSort(field, geohashes, Nil)

    def points(first: GeoPoint, rest: GeoPoint*): GeoDistanceSort = points(first +: rest)
    def points(points: Iterable[GeoPoint]): GeoDistanceSort =
      GeoDistanceSort(field, Nil, points.toSeq)
  }
}
