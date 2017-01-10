package com.sksamuel.elastic4s.searches.sort

import com.sksamuel.elastic4s.ScriptBuilder
import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.elastic4s.searches.QueryBuilderFn
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import org.elasticsearch.search.sort.ScriptSortBuilder.ScriptSortType
import org.elasticsearch.search.sort.{ScriptSortBuilder, SortBuilders, SortMode, SortOrder}

case class ScriptSortDefinition(script: ScriptDefinition,
                                scriptSortType: ScriptSortType) extends SortDefinition[ScriptSortBuilder] {

  val builder = SortBuilders.scriptSort(ScriptBuilder(script), scriptSortType)

  def sortMode(mode: String): this.type = sortMode(SortMode.valueOf(mode.toUpperCase))
  def sortMode(mode: SortMode): this.type = {
    builder.sortMode(mode)
    this
  }

  def nestedPath(nestedPath: String): this.type = {
    builder.setNestedPath(nestedPath)
    this
  }

  def nestedFilter(nestedFilter: QueryDefinition): this.type = {
    builder.setNestedFilter(QueryBuilderFn(nestedFilter))
    this
  }

  def order(order: SortOrder): this.type = {
    builder.order(order)
    this
  }
}
