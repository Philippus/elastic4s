package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.http.EnumConversions
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.sort.{FieldSortDefinition, GeoDistanceSortDefinition, ScoreSortDefinition, ScriptSortDefinition, SortDefinition}

object SortContentBuilder {
  def apply(sort: SortDefinition): XContentBuilder = sort match {
    case fs: FieldSortDefinition => FieldSortContentBuilder(fs)
    case gs: GeoDistanceSortDefinition => GeoDistanceSortContentBuilder(gs)
    case ss: ScoreSortDefinition => ScoreSortContentBuilder(ss)
    case ss: ScriptSortDefinition => ???
  }
}

object FieldSortContentBuilder {
  def apply(fs: FieldSortDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject(fs.field)

    fs.unmappedType.foreach(builder.field("unmapped_type", _))
    fs.missing.foreach(builder.autofield("missing", _))
    fs.sortMode.map(EnumConversions.sortMode).foreach(builder.field("mode", _))
    builder.field("order", EnumConversions.order(fs.order))
    fs.nestedPath.foreach(builder.field("nested_path", _))
    fs.nestedFilter.map(QueryBuilderFn.apply).map(_.string).foreach(builder.rawField("nested_filter", _))

    builder.endObject().endObject()
  }
}

object ScoreSortContentBuilder {
  def apply(fs: ScoreSortDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject("_score")
    builder.field("order", EnumConversions.order(fs.order))
    builder
  }
}

object GeoDistanceSortContentBuilder {
  def apply(geo: GeoDistanceSortDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("_geo_distance")

    if (geo.points.nonEmpty) {
      val point = geo.points.head
      builder.field(geo.field, s"${point.lat},${point.long}")
      builder.startArray(geo.field)
      geo.points.foreach { point =>
        builder.startArray()
        builder.value(point.long)
        builder.value(point.lat)
        builder.endArray()
      }
      builder.endArray()
    } else if (geo.geohashes.nonEmpty) {
      builder.array(geo.field, geo.geohashes.toArray[String])
    }

    geo.geoDistance.map(EnumConversions.geoDistance).foreach(builder.field("distance_type", _))
    geo.sortMode.map(EnumConversions.sortMode).foreach(builder.field("mode", _))
    geo.order.map(o => builder.field("order", EnumConversions.order(o)))
    geo.nestedPath.foreach(builder.field("nested_path", _))
    geo.nestedFilter.map(QueryBuilderFn.apply).map(_.string).foreach(builder.rawField("nested_filter", _))

    builder
  }
}
