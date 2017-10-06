package com.sksamuel.elastic4s.http.search.queries.geo

import com.sksamuel.elastic4s.http.EnumConversions
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.geo.{GeoPolygonQueryDefinition, GeoShapeQueryDefinition, InlineShape, PreindexedShape}

object GeoShapeQueryBodyFn {

  def apply(q: GeoShapeQueryDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()

    builder.startObject("geo_polygon")
    builder.startObject(q.field)

    q.shape match {

      case InlineShape(shapeType, coords) =>

        builder.startObject("shape")
        builder.field("type", shapeType.getClass.getSimpleName.toLowerCase)
        builder.array("coordinates", coords.map { case (a, b) => Seq(a, b) })
        builder.endObject()

      case PreindexedShape(id, index, tpe, path) =>

        builder.startObject("indexed_shape")
        builder.field("id", id)
        builder.field("index", index.name)
        builder.field("type", tpe)
        builder.field("path", path)
        builder.endObject()
    }

    q.relation.map(_.getClass.getSimpleName.toLowerCase).foreach(builder.field("relation", _))
    q.ignoreUnmapped.foreach(builder.field("ignore_unmapped", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))

    builder.endObject().endObject()
  }
}

object GeoPolyonQueryBodyFn {

  def apply(q: GeoPolygonQueryDefinition): XContentBuilder = {
    val builder = XContentFactory.jsonBuilder()
    builder.startObject("geo_polygon")
    builder.startObject(q.field)

    builder.startArray("points")
    q.points.foreach { point =>
      builder.startObject()
      builder.field("lat", point.lat)
      builder.field("lon", point.long)
      builder.endObject()
    }
    builder.endArray()

    q.ignoreUnmapped.foreach(builder.field("ignore_unmapped", _))
    q.validationMethod.map(EnumConversions.geoValidationMethod).foreach(builder.field("validation_method", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))

    builder.endObject()
    builder.endObject()
    builder.endObject()
  }
}
