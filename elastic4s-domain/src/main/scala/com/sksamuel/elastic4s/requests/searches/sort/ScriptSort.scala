package com.sksamuel.elastic4s.requests.searches.sort

import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.elastic4s.ext.OptionImplicits._

case class ScriptSort(
    script: Script,
    scriptSortType: ScriptSortType,
    sortMode: Option[SortMode] = None,
    @deprecated("use nested", "7.8.2")
    nestedPath: Option[String] = None,
    order: Option[SortOrder] = None,
    @deprecated("use nested", "7.8.2")
    nestedFilter: Option[Query] = None,
    nested: Option[NestedSort] = None
) extends Sort {

  def mode(mode: String): ScriptSort   = sortMode(SortMode.valueOf(mode.toUpperCase))
  def mode(mode: SortMode): ScriptSort = copy(sortMode = mode.some)

  def sortMode(mode: String): ScriptSort   = sortMode(SortMode.valueOf(mode.toUpperCase))
  def sortMode(mode: SortMode): ScriptSort = copy(sortMode = mode.some)

  @deprecated("use nested", "7.8.2")
  def nestedPath(path: String): ScriptSort = copy(nestedPath = path.some, nested = None)

  @deprecated("use nested", "7.8.2")
  def nestedFilter(query: Query): ScriptSort = copy(nestedFilter = query.some, nested = None)

  def nested(nested: NestedSort): ScriptSort = copy(nested = nested.some, nestedPath = None, nestedFilter = None)

  def order(order: SortOrder): ScriptSort     = copy(order = order.some)
  def sortOrder(order: SortOrder): ScriptSort = copy(order = order.some)

  def asc(): ScriptSort  = sortOrder(SortOrder.Asc)
  def desc(): ScriptSort = sortOrder(SortOrder.Desc)
}
