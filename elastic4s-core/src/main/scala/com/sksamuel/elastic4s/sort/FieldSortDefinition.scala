package com.sksamuel.elastic4s.sort

import com.sksamuel.elastic4s.queries.QueryDefinition
import org.elasticsearch.search.sort.{SortBuilders, SortMode, SortOrder}

case class FieldSortDefinition(field: String) extends SortDefinition {

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
