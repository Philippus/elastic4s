package com.sksamuel.elastic4s.searches.queries.geo

import com.sksamuel.elastic4s.searches.GeoPoint
import com.sksamuel.elastic4s.searches.queries.QueryDefinition

case class GeoDistanceRangeQueryDefinition(field: String,
                                           geopoint: GeoPoint,
                                           geoDistance: Option[GeoDistance] = None,
                                           queryName: Option[String] = None,
                                           from: Option[Any] = None,
                                           to: Option[Any] = None,
                                           ignoreUnmapped: Option[Boolean] = None,
                                           validationMethod: Option[GeoValidationMethod] = None,
                                           boost: Option[Float] = None,
                                           includeLower: Option[Boolean] = None,
                                           includeUpper: Option[Boolean] = None) extends QueryDefinition {

  def geoDistance(geoDistance: GeoDistance): GeoDistanceRangeQueryDefinition = copy(geoDistance = Some(geoDistance))

  def from(from: Number): GeoDistanceRangeQueryDefinition = copy(from = Some(from))
  def from(from: String): GeoDistanceRangeQueryDefinition = copy(from = Some(from))

  def to(from: Number): GeoDistanceRangeQueryDefinition = copy(to = Some(to))
  def to(from: String): GeoDistanceRangeQueryDefinition = copy(to = Some(to))

  def includeLower(includeLower: Boolean): GeoDistanceRangeQueryDefinition = copy(includeLower = Some(includeLower))
  def includeUpper(includeUpper: Boolean): GeoDistanceRangeQueryDefinition = copy(includeUpper = Some(includeUpper))

  def ignoreUnmapped(ignoreUnmapped: Boolean): GeoDistanceRangeQueryDefinition =
    copy(ignoreUnmapped = Option(ignoreUnmapped))

  def validationMethod(validationMethod: GeoValidationMethod): GeoDistanceRangeQueryDefinition =
    copy(validationMethod = Option(validationMethod))

  def boost(boost: Float): GeoDistanceRangeQueryDefinition = copy(boost = Option(boost))
  def queryName(queryName: String): GeoDistanceRangeQueryDefinition = copy(queryName = Some(queryName))
}
