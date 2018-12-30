package com.sksamuel.elastic4s.requests.searches.queries.geo

import com.sksamuel.elastic4s.requests.searches.GeoPoint
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class GeoPolygonQuery(field: String,
                           points: Seq[GeoPoint],
                           ignoreUnmapped: Option[Boolean] = None,
                           validationMethod: Option[GeoValidationMethod] = None,
                           boost: Option[Double] = None,
                           queryName: Option[String] = None)
    extends Query {

  def ignoreUnmapped(ignoreUnmapped: Boolean): GeoPolygonQuery = copy(ignoreUnmapped = ignoreUnmapped.some)

  def validationMethod(method: String): GeoPolygonQuery =
    validationMethod(GeoValidationMethod.valueOf(method))
  def validationMethod(method: GeoValidationMethod): GeoPolygonQuery = copy(validationMethod = method.some)

  def boost(boost: Double): GeoPolygonQuery         = copy(boost = Option(boost))
  def queryName(queryName: String): GeoPolygonQuery = copy(queryName = Some(queryName))
}
