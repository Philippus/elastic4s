package com.sksamuel.elastic4s.requests.searches.queries.geo

import com.sksamuel.elastic4s.requests.searches.GeoPoint
import com.sksamuel.elastic4s.requests.searches.queries.geo.Shapes.{Circle, Polygon}
import com.sksamuel.elastic4s.{XContentBuilder, XContentFactory}

import scala.language.implicitConversions

object GeoShapeQueryBodyFn {

  private type Coordinates = Array[Double]
  private implicit def coordinates(point: GeoPoint): Coordinates = Array(point.long, point.lat)

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
      case PointShape(point) =>
        builder.array("coordinates", point)

      case EnvelopeShape(upperLeft, lowerRight) =>
        builder.array("coordinates", Array(upperLeft: Coordinates, lowerRight: Coordinates))

      case MultiPointShape(points) =>
        builder.array("coordinates", points.map(identity[Coordinates](_)).toArray)

      case LineStringShape(first, second, remaining@_*) =>
        val points = first :: second :: remaining.toList
        builder.array("coordinates", points.map(identity[Coordinates](_)).toArray)

      case MultiLineStringShape(points) =>
        builder.array("coordinates", points.map(_.map(identity[Coordinates](_)).toArray).toArray)

      case CircleShape(Circle(point, (radius, unit))) =>
        builder.array("coordinates", point)
        builder.field("radius", unit.toMeters(radius) + "m")

      case PolygonShape(p) =>
        val coords = p.holes.fold(Seq(p.points))(h => Seq(p.points) ++ h)
        builder.array("coordinates", coords.map(_.map(identity[Coordinates](_)).toArray).toArray)

      case MultiPolygonShape(polygons) =>
        val coords = polygons.map {
          case Polygon(points, holes) =>
            holes
              .fold(Seq(points))(h => Seq(points) ++ h)
              .map(_.map(identity[Coordinates](_)).toArray)
              .toArray
        }
        builder.array("coordinates", coords.toArray)
    }
  }

}
