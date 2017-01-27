package com.sksamuel.elastic4s.searches.sort

import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.search.sort.{SortMode, SortOrder}

case class FieldSortDefinition(field: String,
                               missing: Option[Any] = None,
                               unmappedType: Option[String] = None,
                               nestedFilter: Option[QueryDefinition] = None,
                               nestedPath: Option[String] = None,
                               sortMode: Option[SortMode] = None,
                               order: Option[SortOrder] = None
                              ) extends SortDefinition {

  def missing(missing: AnyRef): FieldSortDefinition = copy(missing = missing.some)
  def unmappedType(`type`: String): FieldSortDefinition = copy(unmappedType = `type`.some)

  @deprecated("use sortMode", "5.2.0")
  def mode(mode: String): FieldSortDefinition = sortMode(SortMode.valueOf(mode.toUpperCase))

  @deprecated("use sortMode", "5.2.0")
  def mode(mode: SortMode): FieldSortDefinition = copy(sortMode = mode.some)

  def sortMode(mode: String): FieldSortDefinition = sortMode(SortMode.valueOf(mode.toUpperCase))
  def sortMode(mode: SortMode): FieldSortDefinition = copy(sortMode = mode.some)

  def nestedPath(path: String): FieldSortDefinition = copy(nestedPath = path.some)
  def nestedFilter(query: QueryDefinition): FieldSortDefinition = copy(nestedFilter = query.some)

  def order(order: SortOrder): FieldSortDefinition = copy(order = order.some)
}
