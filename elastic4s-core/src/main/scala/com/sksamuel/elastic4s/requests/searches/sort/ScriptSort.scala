package com.sksamuel.elastic4s.requests.searches.sort

import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class ScriptSort(script: Script,
                      scriptSortType: ScriptSortType,
                      sortMode: Option[SortMode] = None,
                      nestedPath: Option[String] = None,
                      order: Option[SortOrder] = None,
                      nestedFilter: Option[Query] = None)
    extends Sort {

  def mode(mode: String): ScriptSort   = sortMode(SortMode.valueOf(mode.toUpperCase))
  def mode(mode: SortMode): ScriptSort = copy(sortMode = mode.some)

  def sortMode(mode: String): ScriptSort   = sortMode(SortMode.valueOf(mode.toUpperCase))
  def sortMode(mode: SortMode): ScriptSort = copy(sortMode = mode.some)

  def nestedPath(path: String): ScriptSort   = copy(nestedPath = path.some)
  def nestedFilter(query: Query): ScriptSort = copy(nestedFilter = query.some)

  def order(order: SortOrder): ScriptSort     = copy(order = order.some)
  def sortOrder(order: SortOrder): ScriptSort = copy(order = order.some)

  def asc(): ScriptSort  = sortOrder(SortOrder.Asc)
  def desc(): ScriptSort = sortOrder(SortOrder.Desc)
}
