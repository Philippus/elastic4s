package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.requests.searches.sort.{FieldSort, GeoDistanceSort, ScoreSort, ScriptSort, Sort}
import com.sksamuel.elastic4s.{EnumConversions, XContentBuilder, XContentFactory}

object SortBuilderFn {
  def apply(sort: Sort): XContentBuilder = sort match {
    case fs: FieldSort       => FieldSortBuilderFn(fs)
    case gs: GeoDistanceSort => GeoDistanceSortBuilderFn(gs)
    case ss: ScoreSort       => ScoreSortBuilderFn(ss)
    case scrs: ScriptSort    => ScriptSortBuilderFn(scrs)
  }
}

object FieldSortBuilderFn {
  def apply(fs: FieldSort): XContentBuilder = {

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

object ScoreSortBuilderFn {
  def apply(fs: ScoreSort): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder().startObject("_score")
    builder.field("order", EnumConversions.order(fs.order))
    builder
  }
}

object GeoDistanceSortBuilderFn {
  def apply(geo: GeoDistanceSort): XContentBuilder = {

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
    } else if (geo.geohashes.nonEmpty)
      builder.array(geo.field, geo.geohashes.toArray[String])

    geo.geoDistance.map(EnumConversions.geoDistance).foreach(builder.field("distance_type", _))
    geo.sortMode.map(EnumConversions.sortMode).foreach(builder.field("mode", _))
    geo.order.map(o => builder.field("order", EnumConversions.order(o)))
    geo.unit.map(EnumConversions.unit).foreach(builder.field("unit", _))
    geo.nestedPath.foreach(builder.field("nested_path", _))
    geo.nestedFilter.map(QueryBuilderFn.apply).map(_.string).foreach(builder.rawField("nested_filter", _))

    builder
  }
}

object ScriptSortBuilderFn {

  def apply(scriptSort: ScriptSort): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder().startObject("_script")

    builder.startObject("script")
    builder.field(scriptSort.script.scriptType.toString.toLowerCase, scriptSort.script.script)
    builder.field("lang", scriptSort.script.lang.getOrElse("painless"))
    if (scriptSort.script.params.nonEmpty)
      builder.autofield("params", scriptSort.script.params)
    builder.endObject()

    builder.field("type", scriptSort.scriptSortType.toString.toLowerCase)

    scriptSort.order.map(a => builder.field("order", EnumConversions.order(a)))
    scriptSort.sortMode.map(a => builder.field("mode", EnumConversions.sortMode(a)))
    scriptSort.nestedPath.map(a => builder.field("nested_path", a))

    builder.endObject()
  }

}
