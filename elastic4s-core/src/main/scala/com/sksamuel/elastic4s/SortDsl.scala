package com.sksamuel.elastic4s

import org.elasticsearch.common.geo.GeoPoint
import org.elasticsearch.search.sort.ScriptSortBuilder.ScriptSortType

trait SortDsl {

  def scoreSort(): ScoreSortDefinition = ScoreSortDefinition()

  def scriptSort(script: ScriptDefinition) = new {
    def typed(`type`: ScriptSortType): ScriptSortDefinition = ScriptSortDefinition(script, `type`)
  }

  def fieldSort(field: String) = FieldSortDefinition(field)

  def geoSort(field: String) = new {
    def points(first: GeoPoint, rest: GeoPoint*): GeoDistanceSortDefinition = points(first +: rest)
    def points(points: Iterable[GeoPoint]): GeoDistanceSortDefinition =
      new GeoDistanceSortDefinition(field, points.toSeq)
  }

}
