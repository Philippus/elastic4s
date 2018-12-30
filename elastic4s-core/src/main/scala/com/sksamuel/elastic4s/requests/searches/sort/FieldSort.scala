package com.sksamuel.elastic4s.requests.searches.sort

import com.sksamuel.elastic4s.requests.searches.queries.Query
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

case class FieldSort(field: String,
                     missing: Option[Any] = None,
                     unmappedType: Option[String] = None,
                     nestedFilter: Option[Query] = None,
                     nestedPath: Option[String] = None,
                     sortMode: Option[SortMode] = None,
                     order: SortOrder = SortOrder.Asc)
    extends Sort {

  def missing(missing: AnyRef): FieldSort     = copy(missing = missing.some)
  def unmappedType(`type`: String): FieldSort = copy(unmappedType = `type`.some)

  def mode(mode: String): FieldSort   = sortMode(SortMode.valueOf(mode.toUpperCase))
  def mode(mode: SortMode): FieldSort = copy(sortMode = mode.some)

  def sortMode(mode: String): FieldSort   = sortMode(SortMode.valueOf(mode.toUpperCase))
  def sortMode(mode: SortMode): FieldSort = copy(sortMode = mode.some)

  def nestedPath(path: String): FieldSort   = copy(nestedPath = path.some)
  def nestedFilter(query: Query): FieldSort = copy(nestedFilter = query.some)

  def order(order: SortOrder): FieldSort     = copy(order = order)
  def sortOrder(order: SortOrder): FieldSort = copy(order = order)
  def desc(): FieldSort                      = copy(order = SortOrder.Desc)
  def asc(): FieldSort                       = copy(order = SortOrder.Asc)
}
