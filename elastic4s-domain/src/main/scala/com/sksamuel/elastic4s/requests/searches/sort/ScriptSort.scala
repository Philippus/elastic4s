package com.sksamuel.elastic4s.requests.searches.sort

import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.elastic4s.ext.OptionImplicits._

case class ScriptSort(
    script: Script,
    scriptSortType: ScriptSortType,
    sortMode: Option[SortMode] = None,
    order: Option[SortOrder] = None,
    nested: Option[NestedSort] = None
) extends Sort {

  def mode(mode: String): ScriptSort   = sortMode(SortMode.valueOf(mode.toUpperCase))
  def mode(mode: SortMode): ScriptSort = copy(sortMode = mode.some)

  def sortMode(mode: String): ScriptSort   = sortMode(SortMode.valueOf(mode.toUpperCase))
  def sortMode(mode: SortMode): ScriptSort = copy(sortMode = mode.some)

  def nested(nested: NestedSort): ScriptSort = copy(nested = nested.some)

  def order(order: SortOrder): ScriptSort     = copy(order = order.some)
  def sortOrder(order: SortOrder): ScriptSort = copy(order = order.some)

  def asc(): ScriptSort  = sortOrder(SortOrder.Asc)
  def desc(): ScriptSort = sortOrder(SortOrder.Desc)
}
