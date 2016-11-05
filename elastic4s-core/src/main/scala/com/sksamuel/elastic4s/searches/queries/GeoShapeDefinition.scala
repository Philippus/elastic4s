package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.common.geo.builders.ShapeBuilder
import org.elasticsearch.common.geo.{ShapeRelation, SpatialStrategy}
import org.elasticsearch.index.query.QueryBuilders

case class GeoShapeDefinition(field: String,
                              shape: ShapeBuilder,
                              relation: Option[ShapeRelation] = None,
                              boost: Option[Float] = None,
                              queryName: Option[String] = None,
                              strategy: Option[SpatialStrategy] = None,
                              indexedShapeIndex: Option[String] = None,
                              indexedShapePath: Option[String] = None,
                              ignoreUnmapped: Option[Boolean] = None) {

  def builder = {
    val builder = QueryBuilders.geoShapeQuery(field, shape)
    ignoreUnmapped.foreach(builder.ignoreUnmapped)
    indexedShapeIndex.foreach(builder.indexedShapeIndex)
    indexedShapePath.foreach(builder.indexedShapePath)
    relation.foreach(builder.relation)
    strategy.foreach(builder.strategy)
    boost.foreach(builder.boost)
    queryName.foreach(builder.queryName)
    builder
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
