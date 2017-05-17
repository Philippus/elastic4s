package com.sksamuel.elastic4s.script

import com.sksamuel.elastic4s.EnumConversions
import com.sksamuel.elastic4s.searches.sort.{FieldSortDefinition, GeoDistanceSortDefinition, ScoreSortDefinition, ScriptSortDefinition, SortDefinition}
import org.elasticsearch.search.sort.{SortBuilder, SortBuilders}

object SortBuilderFn {

  def apply(sort: SortDefinition): SortBuilder[_ <: SortBuilder[_]] = sort match {
    case script: ScriptSortDefinition => ScriptSortBuilderFn(script)
    case ScoreSortDefinition(order) => SortBuilders.scoreSort().order(EnumConversions.sortOrder(order))
    case field: FieldSortDefinition => FieldSortBuilderFn(field)
    case geo: GeoDistanceSortDefinition => GeoDistanceSortBuilderFn(geo)
  }
}
