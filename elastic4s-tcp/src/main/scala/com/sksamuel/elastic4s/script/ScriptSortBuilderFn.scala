package com.sksamuel.elastic4s.script

import com.sksamuel.elastic4s.searches.QueryBuilderFn
import com.sksamuel.elastic4s.searches.sort.ScriptSortDefinition
import com.sksamuel.elastic4s.{EnumConversions, ScriptBuilder}
import org.elasticsearch.search.sort.{ScriptSortBuilder, SortBuilders}

object ScriptSortBuilderFn {

  import com.sksamuel.elastic4s.EnumConversions._

  def apply(d: ScriptSortDefinition): ScriptSortBuilder = {
    val builder = SortBuilders.scriptSort(ScriptBuilder(d.script), d.scriptSortType)
    d.nestedFilter.map(QueryBuilderFn.apply).foreach(builder.setNestedFilter)
    d.order.map(EnumConversions.sortOrder).foreach(builder.order)
    d.nestedPath.foreach(builder.setNestedPath)
    d.sortMode.map(EnumConversions.sortMode).foreach(builder.sortMode)
    builder
  }
}
