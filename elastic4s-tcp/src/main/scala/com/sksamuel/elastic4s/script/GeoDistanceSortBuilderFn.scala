package com.sksamuel.elastic4s.script

import com.sksamuel.elastic4s.EnumConversions
import com.sksamuel.elastic4s.searches.QueryBuilderFn
import com.sksamuel.elastic4s.searches.sort.GeoDistanceSortDefinition
import org.elasticsearch.search.sort.{GeoDistanceSortBuilder, SortBuilders}

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
