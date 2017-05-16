package com.sksamuel.elastic4s.script

import com.sksamuel.elastic4s.{EnumConversions, ScriptBuilder}
import com.sksamuel.elastic4s.searches.QueryBuilderFn
import com.sksamuel.elastic4s.searches.sort.{FieldSortDefinition, GeoDistanceSortDefinition, ScoreSortDefinition, ScriptSortDefinition, SortDefinition}
import org.elasticsearch.search.sort.{FieldSortBuilder, GeoDistanceSortBuilder, ScriptSortBuilder, SortBuilder, SortBuilders}

object SortBuilderFn {

  import com.sksamuel.elastic4s.EnumConversions._

  def apply(sort: SortDefinition): SortBuilder[_] = sort match {
    case script: ScriptSortDefinition => ScriptSortBuilderFn(script)
    case ScoreSortDefinition(order) => SortBuilders.scoreSort().order(order)
    case field: FieldSortDefinition => FieldSortBuilderFn(field)
    case geo: GeoDistanceSortDefinition => GeoDistanceSortBuilderFn(geo)
  }
}

object GeoDistanceSortBuilderFn {

  def apply(d: GeoDistanceSortDefinition): GeoDistanceSortBuilder = {

    val points: Seq[org.elasticsearch.common.geo.GeoPoint] = d.points.map(EnumConversions.geo)
    val builder = if (d.geohashes.nonEmpty) {
      SortBuilders.geoDistanceSort(d.field, d.geohashes: _*).points(points: _*)
    } else {
      SortBuilders.geoDistanceSort(d.field, points: _*)
    }
    d.nestedFilter.map(QueryBuilderFn.apply).foreach(builder.setNestedFilter)
    d.validation.map(EnumConversions.geoValidationMethod).foreach(builder.validation)
    d.geoDistance.map(EnumConversions.geoDistance).foreach(builder.geoDistance)
    d.unit.map(EnumConversions.distanceUnit).foreach(builder.unit)
    d.order.map(EnumConversions.sortOrder).foreach(builder.order)
    d.nestedPath.foreach(builder.setNestedPath)
    d.sortMode.map(EnumConversions.sortMode).foreach(builder.sortMode)
    builder
  }
}

object FieldSortBuilderFn {

  import com.sksamuel.elastic4s.EnumConversions._

  def apply(d: FieldSortDefinition): FieldSortBuilder = {
    val builder = SortBuilders.fieldSort(d.field)
    d.nestedFilter.map(QueryBuilderFn.apply).foreach(builder.setNestedFilter)
    d.unmappedType.foreach(builder.unmappedType)
    d.missing.foreach(builder.missing)
    builder.order(d.order)
    d.nestedPath.foreach(builder.setNestedPath)
    d.sortMode.map(EnumConversions.sortMode).foreach(builder.sortMode)
    builder
  }
}

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
