package com.sksamuel.elastic4s.sort

import com.sksamuel.elastic4s.ScriptDefinition
import com.sksamuel.elastic4s.queries.QueryDefinition
import org.elasticsearch.search.sort.{ScriptSortBuilder, SortBuilders, SortMode, SortOrder}

case class ScriptSortDefinition(script: ScriptDefinition, `type`: ScriptSortBuilder.ScriptSortType) extends SortDefinition {

  val builder = SortBuilders.scriptSort(script.toJavaAPI, `type`)

  def sortMode(sortMode: SortMode): this.type = {
    builder.sortMode(sortMode)
    this
  }

  def nestedPath(nestedPath: String): this.type = {
    builder.setNestedPath(nestedPath)
    this
  }

  def nestedFilter(nestedFilter: QueryDefinition): this.type = {
    builder.setNestedFilter(nestedFilter.builder)
    this
  }

  def order(order: SortOrder): this.type = {
    builder.order(order)
    this
  }
}
