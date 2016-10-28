package com.sksamuel.elastic4s2

import com.sksamuel.elastic4s2.search.QueryDefinition
import org.elasticsearch.common.geo.GeoDistance
import org.elasticsearch.common.geo.GeoPoint
import org.elasticsearch.search.sort.ScriptSortBuilder.ScriptSortType
import org.elasticsearch.search.sort.FieldSortBuilder
import org.elasticsearch.search.sort.GeoDistanceSortBuilder
import org.elasticsearch.search.sort.ScoreSortBuilder
import org.elasticsearch.search.sort.ScriptSortBuilder
import org.elasticsearch.search.sort.SortBuilder
import org.elasticsearch.search.sort.SortBuilders
import org.elasticsearch.search.sort.SortMode
import org.elasticsearch.search.sort.SortOrder

trait SortDefinition[T <: SortBuilder[T]] {
  def builder: SortBuilder[T]
}

case class FieldSortDefinition(field: String) extends SortDefinition[FieldSortBuilder] {

  val builder = SortBuilders.fieldSort(field)

  def missing(missing: AnyRef) = {
    builder.missing(missing)
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

  def mode(mode: SortMode) = {
    builder.sortMode(mode)
    this
  }

  def order(order: SortOrder) = {
    builder.order(order)
    this
  }
}

case class ScriptSortDefinition(script: ScriptDefinition,
                                scriptSortType: ScriptSortType) extends SortDefinition[ScriptSortBuilder] {

  val builder = SortBuilders.scriptSort(script.toJavaAPI, scriptSortType)

  def sortMode(sortMode: SortMode): this.type = {
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
}

class GeoDistanceSortDefinition(field: String,
                                points: Seq[GeoPoint]) extends SortDefinition[GeoDistanceSortBuilder] {

  val builder = SortBuilders.geoDistanceSort(field, points: _*)

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

  def geoDistance(geoDistance: GeoDistance): this.type = {
    builder.geoDistance(geoDistance)
    this
  }
}

case class ScoreSortDefinition() extends SortDefinition[ScoreSortBuilder] {

  val builder = SortBuilders.scoreSort()

  def order(order: SortOrder) = {
    builder.order(order)
    this
  }
}
