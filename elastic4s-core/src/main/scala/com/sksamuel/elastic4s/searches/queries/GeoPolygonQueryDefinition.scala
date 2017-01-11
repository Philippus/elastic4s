package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.exts.OptionImplicits._
import com.sksamuel.elastic4s.GeoPoint

case class GeoPolygonQueryDefinition(field: String,
                                     points: Seq[GeoPoint],
                                     ignoreUnmapped: Option[Boolean] = None,
                                     validationMethod: Option[String] = None,
                                     boost: Option[Float] = None,
                                     queryName: Option[String] = None)
  extends QueryDefinition {

  def ignoreUnmapped(ignoreUnmapped: Boolean): GeoPolygonQueryDefinition = copy(ignoreUnmapped = ignoreUnmapped.some)
  def validationMethod(method: String): GeoPolygonQueryDefinition = copy(validationMethod = method.some)
  def boost(boost: Float): GeoPolygonQueryDefinition = copy(boost = Option(boost))
  def queryName(queryName: String): GeoPolygonQueryDefinition = copy(queryName = Some(queryName))
}
