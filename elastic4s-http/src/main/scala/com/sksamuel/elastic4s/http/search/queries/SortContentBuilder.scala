package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.http.EnumConversions
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.sort.{FieldSortDefinition, GeoDistanceSortDefinition, ScoreSortDefinition, SortDefinition}

object SortContentBuilder {
  def apply(sort: SortDefinition): XContentBuilder = sort match {
    case fs: FieldSortDefinition => FieldSortContentBuilder(fs)
    case gs: GeoDistanceSortDefinition => GeoDistanceSortContentBuilder(gs)
    case ss: ScoreSortDefinition => ScoreSortContentBuilder(ss)
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
    builder.endObject().endObject()
  }
}

object GeoDistanceSortContentBuilder {
  def apply(fs: GeoDistanceSortDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("_geo_distance")

    if(fs.points.nonEmpty) {
      val point = fs.points.head
      builder.field(fs.field, s"${point.lat},${point.long}")
    }
    fs.sortMode.map(EnumConversions.sortMode).foreach(builder.field("mode", _))
    fs.order.map(o => builder.field("order", EnumConversions.order(o)))
    fs.nestedPath.foreach(builder.field("nested_path", _))
    fs.nestedFilter.map(QueryBuilderFn.apply).map(_.string).foreach(builder.rawField("nested_filter", _))

    builder.endObject().endObject()
  }
}
