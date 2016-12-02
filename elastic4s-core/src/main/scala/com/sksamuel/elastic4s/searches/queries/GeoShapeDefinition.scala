package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.searches.QueryDefinition
import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.common.geo.{ShapeRelation, SpatialStrategy}
import org.elasticsearch.index.query.GeoShapeQueryBuilder

case class GeoShapeDefinition(field: String,
                              _builder: GeoShapeQueryBuilder,
                              relation: Option[ShapeRelation] = None,
                              boost: Option[Float] = None,
                              queryName: Option[String] = None,
                              strategy: Option[SpatialStrategy] = None,
                              indexedShapeIndex: Option[String] = None,
                              indexedShapePath: Option[String] = None,
                              ignoreUnmapped: Option[Boolean] = None) extends QueryDefinition {

  override def builder = {
    ignoreUnmapped.foreach(_builder.ignoreUnmapped)
    indexedShapeIndex.foreach(_builder.indexedShapeIndex)
    indexedShapePath.foreach(_builder.indexedShapePath)
    relation.foreach(_builder.relation)
    strategy.foreach(_builder.strategy)
    boost.foreach(_builder.boost)
    queryName.foreach(_builder.queryName)
    _builder
  }

  def relation(relation: ShapeRelation): GeoShapeDefinition = copy(relation = relation.some)
  def boost(boost: Float): GeoShapeDefinition = copy(boost = boost.some)
  def queryName(queryName: String): GeoShapeDefinition = copy(queryName = queryName.some)
  def strategy(strategy: SpatialStrategy): GeoShapeDefinition = copy(strategy = strategy.some)

  def indexedShapeIndex(indexedShapeIndex: String): GeoShapeDefinition =
    copy(indexedShapeIndex = indexedShapeIndex.some)

  def indexedShapePath(indexedShapePath: String): GeoShapeDefinition = copy(indexedShapePath = indexedShapePath.some)
  def ignoreUnmapped(ignore: Boolean): GeoShapeDefinition = copy(ignoreUnmapped = ignore.some)

}
