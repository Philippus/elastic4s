package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.script.ScriptDefinition
import org.elasticsearch.common.geo.GeoPoint
import org.elasticsearch.search.sort.ScriptSortBuilder.ScriptSortType

trait SortDsl {

  def scoreSort(): ScoreSortDefinition = ScoreSortDefinition()

  def scriptSort(script: ScriptDefinition): ScriptSortExpectsType = new ScriptSortExpectsType(script)
  class ScriptSortExpectsType(script: ScriptDefinition) {
    def typed(`type`: String): ScriptSortDefinition = typed(ScriptSortType.valueOf(`type`))
    def typed(`type`: ScriptSortType): ScriptSortDefinition = ScriptSortDefinition(script, `type`)
  }

  def fieldSort(field: String) = FieldSortDefinition(field)

  def geoSort(field: String): GeoSortExpectsPoints = new GeoSortExpectsPoints(field)
  class GeoSortExpectsPoints(field: String) {
    def points(first: String, rest: String*): GeoDistanceSortDefinition = points(first +: rest)
    def points(geohashes: Seq[String]): GeoDistanceSortDefinition =
      new GeoDistanceSortDefinition(field, geohashes, Nil)

    def points(first: GeoPoint, rest: GeoPoint*): GeoDistanceSortDefinition = points(first +: rest)
    def points(points: Iterable[GeoPoint]): GeoDistanceSortDefinition =
      new GeoDistanceSortDefinition(field, Nil, points.toSeq)
  }

}
