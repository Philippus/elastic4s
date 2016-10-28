package com.sksamuel.elastic4s.query

import com.sksamuel.elastic4s.QueryDefinition
import org.elasticsearch.common.geo.{GeoDistance, GeoPoint}
import org.elasticsearch.index.query.{GeoDistanceRangeQueryBuilder, GeoValidationMethod, QueryBuilders}

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

  def builder: GeoDistanceRangeQueryBuilder = {
    val builder = QueryBuilders.geoDistanceRangeQuery(field, geopoint)
    geoDistance.foreach(builder.geoDistance)
    includeLower.foreach(builder.includeLower)
    includeUpper.foreach(builder.includeUpper)
    from.foreach {
      case number: Number => builder.from(number)
      case str: String => builder.from(str)
    }
    to.foreach {
      case number: Number => builder.to(number)
      case str: String => builder.to(str)
    }
    boost.foreach(builder.boost)
    queryName.foreach(builder.queryName)
    validationMethod.foreach(builder.setValidationMethod)
    ignoreUnmapped.foreach(builder.ignoreUnmapped)
    queryName.foreach(builder.to)
    builder
  }

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
