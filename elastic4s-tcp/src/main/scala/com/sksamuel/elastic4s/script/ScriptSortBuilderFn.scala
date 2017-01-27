package com.sksamuel.elastic4s.script

import com.sksamuel.elastic4s.ScriptBuilder
import com.sksamuel.elastic4s.searches.QueryBuilderFn
import com.sksamuel.elastic4s.searches.sort._
import org.elasticsearch.search.sort._

object SortBuilderFn {
  def apply(sort: SortDefinition): SortBuilder[_] = sort match {
    case script: ScriptSortDefinition => ScriptSortBuilderFn(script)
    case ScoreSortDefinition(order) => SortBuilders.scoreSort().order(order)
    case field: FieldSortDefinition => FieldSortBuilderFn(field)
    case geo: GeoDistanceSortDefinition => GeoDistanceSortBuilderFn(geo)
  }
}


object GeoDistanceSortBuilderFn {
  def apply(d: GeoDistanceSortDefinition): GeoDistanceSortBuilder = {
    val builder = if (d.geohashes.nonEmpty) {
      SortBuilders.geoDistanceSort(d.field, d.geohashes: _*).points(d.points: _*)
    } else {
      SortBuilders.geoDistanceSort(d.field, d.points: _*)
    }
    d.nestedFilter.map(QueryBuilderFn.apply).foreach(builder.setNestedFilter)
    d.validation.foreach(builder.validation)
    d.geoDistance.foreach(builder.geoDistance)
    d.unit.foreach(builder.unit)
    d.order.foreach(builder.order)
    d.nestedPath.foreach(builder.setNestedPath)
    d.sortMode.foreach(builder.sortMode)
    builder
  }
}

object FieldSortBuilderFn {
  def apply(d: FieldSortDefinition): FieldSortBuilder = {
    val builder = SortBuilders.fieldSort(d.field)
    d.nestedFilter.map(QueryBuilderFn.apply).foreach(builder.setNestedFilter)
    d.unmappedType.foreach(builder.unmappedType)
    d.missing.foreach(builder.missing)
    d.order.foreach(builder.order)
    d.nestedPath.foreach(builder.setNestedPath)
    d.sortMode.foreach(builder.sortMode)
    builder
  }
}

object ScriptSortBuilderFn {
  def apply(d: ScriptSortDefinition): ScriptSortBuilder = {
    val builder = SortBuilders.scriptSort(ScriptBuilder(d.script), d.scriptSortType)
    d.nestedFilter.map(QueryBuilderFn.apply).foreach(builder.setNestedFilter)
    d.order.foreach(builder.order)
    d.nestedPath.foreach(builder.setNestedPath)
    d.sortMode.foreach(builder.sortMode)
    builder
  }
}
