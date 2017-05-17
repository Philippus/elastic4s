package com.sksamuel.elastic4s.script

import com.sksamuel.elastic4s.EnumConversions
import com.sksamuel.elastic4s.searches.QueryBuilderFn
import com.sksamuel.elastic4s.searches.sort.FieldSortDefinition
import org.elasticsearch.search.sort.{FieldSortBuilder, SortBuilders}

object FieldSortBuilderFn {

  def apply(d: FieldSortDefinition): FieldSortBuilder = {
    val builder = SortBuilders.fieldSort(d.field)
    d.nestedFilter.map(QueryBuilderFn.apply).foreach(builder.setNestedFilter)
    d.unmappedType.foreach(builder.unmappedType)
    d.missing.foreach(builder.missing)
    builder.order(EnumConversions.sortOrder(d.order))
    d.nestedPath.foreach(builder.setNestedPath)
    d.sortMode.map(EnumConversions.sortMode).foreach(builder.sortMode)
    builder
  }
}
