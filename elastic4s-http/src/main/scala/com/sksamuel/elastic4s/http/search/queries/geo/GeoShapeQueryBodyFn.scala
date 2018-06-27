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

      case InlineShape(shape: SingleShape) =>
        builder.rawField("shape", buildSingleShape(shape))

      case InlineShape(s @ GeometryCollectionShape(shapes)) =>
        builder.startObject("shape")
        builder.field("type", s.geoShapeType.toString.toLowerCase)
        builder.array("geometries", shapes.map(buildSingleShape).toArray)
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

  private def buildSingleShape(shape: SingleShape): XContentBuilder =
    shape match {
      case s @ PointShape(GeoPoint(x, y)) =>
        val builder = XContentFactory.jsonBuilder()
        builder.field("type", s.geoShapeType.toString.toLowerCase)
        builder.array("coordinates", Array(x, y))
        builder

      case s @ EnvelopeShape(GeoPoint(ulX, ulY), GeoPoint(lrX, lrY)) =>
        val builder = XContentFactory.jsonBuilder()
        builder.field("type", s.geoShapeType.toString.toLowerCase)
        builder.array("coordinates", Array(Array(ulX, ulY), Array(lrX, lrY)))
        builder

      case s @ MultiPointShape(points) =>
        val builder = XContentFactory.jsonBuilder()
        builder.field("type", s.geoShapeType.toString.toLowerCase)
        builder.array("coordinates", points.map { case GeoPoint(a, b) => Array(a, b) }.toArray)
        builder

      case s @ LineStringShape(first, second, remaining @ _*) =>
        val builder = XContentFactory.jsonBuilder()
        builder.field("type", s.geoShapeType.toString.toLowerCase)
        val points = first :: second :: remaining.toList
        builder.array("coordinates", points.map { case GeoPoint(a, b) => Array(a, b) }.toArray)
        builder

      case s @ MultiLineStringShape(points) =>
        val builder = XContentFactory.jsonBuilder()
        builder.field("type", s.geoShapeType.toString.toLowerCase)
        builder.array("coordinates", points.map(_.map { case GeoPoint(a, b) => Array(a, b) }.toArray).toArray)
        builder

      case s @ CircleShape(Circle(GeoPoint(x, y), (radius, unit))) =>
        val builder = XContentFactory.jsonBuilder()
        builder.field("type", s.geoShapeType.toString.toLowerCase)
        builder.array("coordinates", Array(x, y))
        builder.field("radius", unit.toMeters(radius) + "m")
        builder

      case s @ PolygonShape(p) =>
        val builder = XContentFactory.jsonBuilder()
        builder.field("type", s.geoShapeType.toString.toLowerCase)
        val coords = p.holes.fold(Seq(p.points))(h => Seq(p.points) ++ h)
        builder.array("coordinates", coords.map(_.map { case GeoPoint(a, b) => Array(a, b) }.toArray).toArray)
        builder

      case s @ MultiPolygonShape(polygons) =>
        val builder = XContentFactory.jsonBuilder()
        builder.field("type", s.geoShapeType.toString.toLowerCase)
        val coords = polygons.map {
          case Polygon(points, holes) =>
            holes
              .fold(Seq(points))(h => Seq(points) ++ h)
              .map(_.map { case GeoPoint(a, b) => Array(a, b) }.toArray)
              .toArray
        }
        builder.array("coordinates", coords.toArray)
        builder
    }
}
