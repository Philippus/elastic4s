package com.sksamuel.elastic4s.http.search.queries.geo

import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.GeoPoint
import com.sksamuel.elastic4s.searches.queries.geo.Shapes.{Circle, Polygon}
import com.sksamuel.elastic4s.searches.queries.geo._

object GeoShapeQueryBodyFn {

  def apply(q: GeoShapeQuery): XContentBuilder = {

    val builder = XContentFactory.jsonBuilder()

    builder.startObject("geo_shape")
    builder.startObject(q.field)

    q.shape match {

      case InlineShape(shape: ShapeDefinition) =>
        builder.startObject("shape")
        buildShapeDefinition(shape, builder)
        builder.endObject()

      case PreindexedShape(id, index, tpe, path) =>
        builder.startObject("indexed_shape")
        builder.field("id", id)
        builder.field("index", index.name)
        builder.field("type", tpe)
        builder.field("path", path)
        builder.endObject()
    }

    q.relation.map(_.getClass.getSimpleName.toLowerCase.stripSuffix("$")).foreach(builder.field("relation", _))
    q.ignoreUnmapped.foreach(builder.field("ignore_unmapped", _))
    q.boost.foreach(builder.field("boost", _))
    q.queryName.foreach(builder.field("_name", _))

    builder.endObject().endObject()
  }

  private def buildShapeDefinition(shape: ShapeDefinition, builder: XContentBuilder): XContentBuilder = {
    shape match {

      case single: SingleShape =>
        buildSingleShape(single, builder)

      case collection: GeometryCollectionShape =>
        builder.field("type", shape.geoShapeType.toString.toLowerCase)
        builder.startArray("geometries")
        collection.shapes.foreach(s => {
          builder.startObject()
          buildShapeDefinition(s, builder)
          builder.endObject()
        })
        builder.endArray()
    }
  }

  private def buildSingleShape(shape: SingleShape, builder: XContentBuilder): XContentBuilder = {
    builder.field("type", shape.geoShapeType.toString.toLowerCase)
    shape match {
      case s@PointShape(GeoPoint(x, y)) =>
        builder.array("coordinates", Array(x, y))

      case s@EnvelopeShape(GeoPoint(ulX, ulY), GeoPoint(lrX, lrY)) =>
        builder.array("coordinates", Array(Array(ulX, ulY), Array(lrX, lrY)))

      case s@MultiPointShape(points) =>
        builder.array("coordinates", points.map { case GeoPoint(a, b) => Array(a, b) }.toArray)

      case s@LineStringShape(first, second, remaining@_*) =>
        val points = first :: second :: remaining.toList
        builder.array("coordinates", points.map { case GeoPoint(a, b) => Array(a, b) }.toArray)

      case s@MultiLineStringShape(points) =>
        builder.array("coordinates", points.map(_.map { case GeoPoint(a, b) => Array(a, b) }.toArray).toArray)

      case s@CircleShape(Circle(GeoPoint(x, y), (radius, unit))) =>
        builder.array("coordinates", Array(x, y))
        builder.field("radius", unit.toMeters(radius) + "m")

      case s@PolygonShape(p) =>
        val coords = p.holes.fold(Seq(p.points))(h => Seq(p.points) ++ h)
        builder.array("coordinates", coords.map(_.map { case GeoPoint(a, b) => Array(a, b) }.toArray).toArray)

      case s@MultiPolygonShape(polygons) =>
        val coords = polygons.map {
          case Polygon(points, holes) =>
            holes
              .fold(Seq(points))(h => Seq(points) ++ h)
              .map(_.map { case GeoPoint(a, b) => Array(a, b) }.toArray)
              .toArray
        }
        builder.array("coordinates", coords.toArray)
    }
  }
}
