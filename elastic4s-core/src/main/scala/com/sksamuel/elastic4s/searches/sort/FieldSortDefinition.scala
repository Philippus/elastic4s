package com.sksamuel.elastic4s.searches.sort

import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.exts.OptionImplicits._

sealed trait SortMode
object SortMode {

  def valueOf(str: String): SortMode = str.toLowerCase match {
    case "avg"    => Avg
    case "max"    => Max
    case "min"    => Min
    case "median" => Median
    case "sum"    => Sum
  }

  case object Avg    extends SortMode
  case object Median extends SortMode
  case object Min    extends SortMode
  case object Max    extends SortMode
  case object Sum    extends SortMode

  def AVG    = Avg
  def MEDIAN = Median
  def MIN    = Min
  def MAX    = Max
  def SUM    = Sum
}

sealed trait SortOrder
object SortOrder {
  case object Asc  extends SortOrder
  case object Desc extends SortOrder

  def DESC = Desc
  def ASC  = Asc
}

case class FieldSortDefinition(field: String,
                               missing: Option[Any] = None,
                               unmappedType: Option[String] = None,
                               nestedFilter: Option[QueryDefinition] = None,
                               nestedPath: Option[String] = None,
                               sortMode: Option[SortMode] = None,
                               order: SortOrder = SortOrder.Asc)
    extends SortDefinition {

  def missing(missing: AnyRef): FieldSortDefinition     = copy(missing = missing.some)
  def unmappedType(`type`: String): FieldSortDefinition = copy(unmappedType = `type`.some)

  def mode(mode: String): FieldSortDefinition   = sortMode(SortMode.valueOf(mode.toUpperCase))
  def mode(mode: SortMode): FieldSortDefinition = copy(sortMode = mode.some)

  def sortMode(mode: String): FieldSortDefinition   = sortMode(SortMode.valueOf(mode.toUpperCase))
  def sortMode(mode: SortMode): FieldSortDefinition = copy(sortMode = mode.some)

  def nestedPath(path: String): FieldSortDefinition             = copy(nestedPath = path.some)
  def nestedFilter(query: QueryDefinition): FieldSortDefinition = copy(nestedFilter = query.some)

  def order(order: SortOrder): FieldSortDefinition     = copy(order = order)
  def sortOrder(order: SortOrder): FieldSortDefinition = copy(order = order)
  def desc(): FieldSortDefinition                      = copy(order = SortOrder.Desc)
  def asc(): FieldSortDefinition                       = copy(order = SortOrder.Asc)
}
