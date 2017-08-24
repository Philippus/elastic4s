//package com.sksamuel.elastic4s.searches.queries.geo
//
//import com.sksamuel.elastic4s.searches.queries.QueryDefinition
//import com.sksamuel.exts.OptionImplicits._
//
//case class GeoShapeDefinition(field: String,
//                              builder: GeoShapeQueryBuilder,
//                              relation: Option[ShapeRelation] = None,
//                              boost: Option[Double] = None,
//                              queryName: Option[String] = None,
//                              strategy: Option[SpatialStrategy] = None,
//                              indexedShapeIndex: Option[String] = None,
//                              indexedShapePath: Option[String] = None,
//                              ignoreUnmapped: Option[Boolean] = None) extends QueryDefinition {
//
//  def relation(relation: ShapeRelation): GeoShapeDefinition = copy(relation = relation.some)
//  def boost(boost: Double): GeoShapeDefinition = copy(boost = boost.some)
//  def queryName(queryName: String): GeoShapeDefinition = copy(queryName = queryName.some)
//  def strategy(strategy: SpatialStrategy): GeoShapeDefinition = copy(strategy = strategy.some)
//
//  def indexedShapeIndex(indexedShapeIndex: String): GeoShapeDefinition =
//    copy(indexedShapeIndex = indexedShapeIndex.some)
//
//  def indexedShapePath(indexedShapePath: String): GeoShapeDefinition = copy(indexedShapePath = indexedShapePath.some)
//  def ignoreUnmapped(ignore: Boolean): GeoShapeDefinition = copy(ignoreUnmapped = ignore.some)
//}
