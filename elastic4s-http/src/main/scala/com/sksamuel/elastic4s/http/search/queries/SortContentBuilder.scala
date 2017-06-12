package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.searches.sort.{FieldSortDefinition, GeoDistanceSortDefinition, ScoreSortDefinition, SortDefinition}
import org.elasticsearch.common.bytes.BytesArray
import org.elasticsearch.common.xcontent.{XContentBuilder, XContentFactory}

object SortContentBuilder {
  def apply(sort: SortDefinition): XContentBuilder = sort match {
    case fs: FieldSortDefinition => FieldSortContentBuilder(fs)
    case gs: GeoDistanceSortDefinition => GeoDistanceSortContentBuilder(gs)
    case ss: ScoreSortDefinition => ScoreSortContentBuilder(ss)
  }
}

object FieldSortContentBuilder {
  def apply(fs: FieldSortDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject(fs.field)
    fs.unmappedType.foreach(builder.field("unmapped_type", _))
    fs.missing.foreach(builder.field("missing", _))
    fs.sortMode.map(_.toString).foreach(builder.field("mode", _))
    builder.field("order", fs.order.toString)
    fs.nestedPath.foreach(builder.field("nested_path", _))
    fs.nestedFilter.map(QueryBuilderFn.apply).map(_.string).map(new BytesArray(_)).foreach(builder.rawField("nested_filter", _))
    builder.endObject()
    builder.endObject()
    builder
  }
}

object ScoreSortContentBuilder {
  def apply(fs: ScoreSortDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()
    builder.startObject()
    builder.startObject("_score")

    builder.field("order", fs.order.toString)
    builder.endObject()
    builder.endObject()
  }
}

object GeoDistanceSortContentBuilder {
  def apply(fs: GeoDistanceSortDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("_geo_distance")

    if (fs.points.nonEmpty) {
      val point = fs.points.head
      builder.field(fs.field, s"${point.lat},${point.lon}")
    }
    fs.sortMode.map(_.toString.toLowerCase).foreach(builder.field("mode", _))
    fs.order.map(o => builder.field("order", o.toString))
    fs.nestedPath.foreach(builder.field("nested_path", _))
    fs.nestedFilter.map(QueryBuilderFn.apply).map(_.string).map(new BytesArray(_)).foreach(builder.rawField("nested_filter", _))

    builder.endObject()
    builder.endObject()
  }
}
