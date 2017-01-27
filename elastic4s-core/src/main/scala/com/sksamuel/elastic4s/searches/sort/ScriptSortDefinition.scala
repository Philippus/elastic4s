package com.sksamuel.elastic4s.searches.sort

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.search.sort.ScriptSortBuilder.ScriptSortType
import org.elasticsearch.search.sort.{SortMode, SortOrder}

case class ScriptSortDefinition(script: ScriptDefinition,
                                scriptSortType: ScriptSortType,
                                sortMode: Option[SortMode] = None,
                                nestedPath: Option[String] = None,
                                order: Option[SortOrder] = None,
                                nestedFilter: Option[QueryDefinition] = None) extends SortDefinition {

  def sortMode(mode: String): ScriptSortDefinition = sortMode(SortMode.valueOf(mode.toUpperCase))
  def sortMode(mode: SortMode): ScriptSortDefinition = copy(sortMode = mode.some)

  def nestedPath(path: String): ScriptSortDefinition = copy(nestedPath = path.some)
  def nestedFilter(query: QueryDefinition): ScriptSortDefinition = copy(nestedFilter = query.some)

  def order(order: SortOrder): ScriptSortDefinition = copy(order = order.some)
}
