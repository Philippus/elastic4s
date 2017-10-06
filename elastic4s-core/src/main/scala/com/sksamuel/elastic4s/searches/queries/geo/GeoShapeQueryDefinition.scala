package com.sksamuel.elastic4s.searches.queries.geo

import com.sksamuel.elastic4s.Index
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.exts.OptionImplicits._

trait Shape
case class InlineShape(geoShapeType: GeoShapeType, coordinates: Seq[(Double, Double)]) extends Shape
case class PreindexedShape(id: String, index: Index, `type`: String, path: String) extends Shape

case class GeoShapeQueryDefinition(field: String,
                                   shape: Shape,
                                   relation: Option[ShapeRelation] = None,
                                   boost: Option[Double] = None,
                                   queryName: Option[String] = None,
                                   strategy: Option[SpatialStrategy] = None,
                                   ignoreUnmapped: Option[Boolean] = None) extends QueryDefinition {

  def relation(relation: ShapeRelation): GeoShapeQueryDefinition = copy(relation = relation.some)
  def boost(boost: Double): GeoShapeQueryDefinition = copy(boost = boost.some)
  def queryName(queryName: String): GeoShapeQueryDefinition = copy(queryName = queryName.some)
  def strategy(strategy: SpatialStrategy): GeoShapeQueryDefinition = copy(strategy = strategy.some)

  def inlineShape(shapeType: GeoShapeType, coords: Seq[(Double, Double)]) = copy(shape = InlineShape(shapeType, coords))
  def preindexedShape(id: String, index: Index, `type`: String, path: String) = copy(shape = PreindexedShape(id, index, `type`, path))

  def ignoreUnmapped(ignore: Boolean): GeoShapeQueryDefinition = copy(ignoreUnmapped = ignore.some)
}

trait GeoShapeType
object GeoShapeType {
  case object POINT extends GeoShapeType
  case object MULTIPOINT extends GeoShapeType
  case object LINESTRING extends GeoShapeType
  case object MULTILINESTRING extends GeoShapeType
  case object POLYGON extends GeoShapeType
  case object MULTIPOLYGON extends GeoShapeType
  case object GEOMETRYCOLLECTION extends GeoShapeType
  case object ENVELOPE extends GeoShapeType
  case object CIRCLE extends GeoShapeType
}

trait ShapeRelation
object ShapeRelation {
  case object INTERSECTS extends ShapeRelation
  case object DISJOINT extends ShapeRelation
  case object WITHIN extends ShapeRelation
  case object CONTAINS extends ShapeRelation
  val intersects = INTERSECTS
  val disjoint = DISJOINT
  val within = WITHIN
  val contains = CONTAINS
}

trait SpatialStrategy
object SpatialStrategy {
  case object Term extends SpatialStrategy
  case object Recursive extends SpatialStrategy
}
