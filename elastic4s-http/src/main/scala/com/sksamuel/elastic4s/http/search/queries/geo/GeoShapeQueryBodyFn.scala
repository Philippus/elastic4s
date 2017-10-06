package com.sksamuel.elastic4s.http.search.queries.geo

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.geo.{GeoShapeQueryDefinition, InlineShape, PreindexedShape}

object GeoShapeQueryBodyFn {

  def apply(q: GeoShapeQueryDefinition): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()

    builder.startObject("geo_polygon")
    builder.startObject(q.field)

    q.shape match {

      case InlineShape(shapeType, coords) =>

        builder.startObject("shape")
        builder.field("type", shapeType.getClass.getSimpleName.toLowerCase)
        builder.array("coordinates", coords.map { case (a, b) => Array(a, b) }.toArray)
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
