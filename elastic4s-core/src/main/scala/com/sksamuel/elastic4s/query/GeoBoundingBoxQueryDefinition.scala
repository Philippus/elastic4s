package com.sksamuel.elastic4s.query

import com.sksamuel.elastic4s.QueryDefinition
import org.elasticsearch.common.geo.GeoPoint
import org.elasticsearch.index.query.{GeoBoundingBoxQueryBuilder, GeoExecType, GeoValidationMethod, QueryBuilders}

case class GeoBoundingBoxQueryDefinition(field: String,
                                         top: Double,
                                         left: Double,
                                         right: Double,
                                         bottom: Double,
                                         geohash: Option[String],
                                         cornersOGC: Option[(GeoPoint, GeoPoint)] = None,
                                         queryName: Option[String] = None,
                                         geoExecType: Option[GeoExecType] = None,
                                         validationMethod: Option[GeoValidationMethod] = None,
                                         ignoreUnmapped: Option[Boolean] = None)
  extends QueryDefinition {

  def builder: GeoBoundingBoxQueryBuilder = {
    val builder = QueryBuilders.geoBoundingBoxQuery(field)
    builder.setCorners(top, left, bottom, right)
    geohash.foreach(builder.setCorners)
    cornersOGC.foreach { case (bl, tr) => builder.setCornersOGC(bl, tr) }
    validationMethod.foreach(builder.setValidationMethod)
    ignoreUnmapped.foreach(builder.ignoreUnmapped)
    queryName.foreach(builder.queryName)
    geoExecType.foreach(builder.`type`)
    builder.`type`()
    builder
  }

  def withGeohash(geohash: String) = copy(geohash = Some(geohash))
  def withCorners(topLeft: GeoPoint, bottomRight: GeoPoint) = copy(top = topLeft.getLat, left = topLeft.getLon, bottom = bottomRight.getLat, right = bottomRight.getLon)
  def withCorners(top: Double, left: Double, bottom: Double, right: Double) = copy(left = left, top = top, right = right, bottom = bottom)
  def withCornersOGC(bottomLeft: String, topRight: String) = withCornersOGC(GeoPoint.fromGeohash(bottomLeft), GeoPoint.fromGeohash(topRight))
  def withCornersOGC(bottomLeft: GeoPoint, topRight: GeoPoint) = copy(cornersOGC = Some(bottomLeft, topRight))
  def withType(geoExecType: GeoExecType) = copy(geoExecType = Option(geoExecType))
  def withIgnoreUnmapped(ignoreUnmapped: Boolean) = copy(ignoreUnmapped = Option(ignoreUnmapped))
  def withValidationMethod(validationMethod: GeoValidationMethod) = copy(validationMethod = Option(validationMethod))
  def withQueryName(queryName: String) = copy(queryName = Option(queryName))
}
