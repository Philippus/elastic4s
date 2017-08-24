package com.sksamuel.elastic4s.searches.queries.geo

import com.sksamuel.elastic4s.searches.GeoPoint
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.exts.OptionImplicits._

case class GeoPolygonQueryDefinition(field: String,
                                     points: Seq[GeoPoint],
                                     ignoreUnmapped: Option[Boolean] = None,
                                     validationMethod: Option[GeoValidationMethod] = None,
                                     boost: Option[Double] = None,
                                     queryName: Option[String] = None) extends QueryDefinition {

  def ignoreUnmapped(ignoreUnmapped: Boolean): GeoPolygonQueryDefinition = copy(ignoreUnmapped = ignoreUnmapped.some)

  def validationMethod(method: String): GeoPolygonQueryDefinition = validationMethod(GeoValidationMethod.valueOf(method))
  def validationMethod(method: GeoValidationMethod): GeoPolygonQueryDefinition = copy(validationMethod = method.some)

  def boost(boost: Double): GeoPolygonQueryDefinition = copy(boost = Option(boost))
  def queryName(queryName: String): GeoPolygonQueryDefinition = copy(queryName = Some(queryName))
}
