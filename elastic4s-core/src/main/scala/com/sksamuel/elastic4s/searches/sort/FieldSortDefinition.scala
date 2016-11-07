package com.sksamuel.elastic4s.searches.sort

import com.sksamuel.elastic4s.searches.QueryDefinition
import org.elasticsearch.search.sort.{FieldSortBuilder, SortBuilders, SortMode, SortOrder}

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

  def mode(m: String): FieldSortDefinition = mode(SortMode.valueOf(m.toUpperCase))
  def mode(mode: SortMode): FieldSortDefinition = {
    builder.sortMode(mode)
    this
  }

  def order(order: SortOrder) = {
    builder.order(order)
    this
  }
}
