package com.sksamuel.elastic4s.requests.searches.sort

import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.elastic4s.ext.OptionImplicits._

case class FieldSort(field: String,
                     missing: Option[Any] = None,
                     unmappedType: Option[String] = None,
                     @deprecated("use nested", "7.8.2")
                     nestedFilter: Option[Query] = None,
                     @deprecated("use nested", "7.8.2")
                     nestedPath: Option[String] = None,
                     sortMode: Option[SortMode] = None,
                     order: SortOrder = SortOrder.Asc,
                     numericType: Option[String] = None,
                     nested: Option[NestedSort] = None) extends Sort {

  def missing(missing: AnyRef): FieldSort = copy(missing = missing.some)
  def unmappedType(`type`: String): FieldSort = copy(unmappedType = `type`.some)

  def mode(mode: String): FieldSort = sortMode(SortMode.valueOf(mode.toUpperCase))
  def mode(mode: SortMode): FieldSort = copy(sortMode = mode.some)

  def sortMode(mode: String): FieldSort = sortMode(SortMode.valueOf(mode.toUpperCase))
  def sortMode(mode: SortMode): FieldSort = copy(sortMode = mode.some)

  @deprecated("use nested", "7.8.2")
  def nestedPath(path: String): FieldSort = copy(nestedPath = path.some, nested = None)

  @deprecated("use nested", "7.8.2")
  def nestedFilter(query: Query): FieldSort = copy(nestedFilter = query.some, nested = None)

  def nested(nested: NestedSort): FieldSort = copy(nested = nested.some, nestedPath = None, nestedFilter = None)

  def numericType(numericType: String): FieldSort = copy(numericType = numericType.some)

  def order(order: SortOrder): FieldSort = copy(order = order)
  def sortOrder(order: SortOrder): FieldSort = copy(order = order)
  def desc(): FieldSort = copy(order = SortOrder.Desc)
  def asc(): FieldSort = copy(order = SortOrder.Asc)
}
