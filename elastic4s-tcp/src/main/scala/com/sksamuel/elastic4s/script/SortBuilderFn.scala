package com.sksamuel.elastic4s.script

import com.sksamuel.elastic4s.EnumConversions
import com.sksamuel.elastic4s.searches.sort._
import org.elasticsearch.search.sort.{SortBuilder, SortBuilders}

object SortBuilderFn {

  def apply[T <: SortBuilder[T]](sort: SortDefinition): SortBuilder[T] = sort match {
    case script: ScriptSortDefinition => ScriptSortBuilderFn(script).asInstanceOf[SortBuilder[T]]
    case ScoreSortDefinition(order) =>
      SortBuilders.scoreSort().order(EnumConversions.sortOrder(order)).asInstanceOf[SortBuilder[T]]
    case field: FieldSortDefinition     => FieldSortBuilderFn(field).asInstanceOf[SortBuilder[T]]
    case geo: GeoDistanceSortDefinition => GeoDistanceSortBuilderFn(geo).asInstanceOf[SortBuilder[T]]
  }
}
