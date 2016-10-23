package com.sksamuel.elastic4s.queries

import com.sksamuel.elastic4s.DefinitionAttributes.{DefinitionAttributeFrom, DefinitionAttributeGt, DefinitionAttributeLat, DefinitionAttributeLon, DefinitionAttributeLt, DefinitionAttributePoint, DefinitionAttributeTo}
import org.elasticsearch.common.geo.GeoDistance
import org.elasticsearch.index.query.QueryBuilders

case class GeoDistanceRangeQueryDefinition(field: String)
  extends QueryDefinition
    with DefinitionAttributeTo
    with DefinitionAttributeFrom
    with DefinitionAttributeLt
    with DefinitionAttributeGt
    with DefinitionAttributeLat
    with DefinitionAttributeLon
    with DefinitionAttributePoint {

  val builder = QueryBuilders.geoDistanceRangeQuery(field)
  val _builder = builder

  def geoDistance(geoDistance: GeoDistance): GeoDistanceRangeQueryDefinition = {
    builder.geoDistance(geoDistance)
    this
  }

  def geohash(geohash: String): GeoDistanceRangeQueryDefinition = {
    builder.geohash(geohash)
    this
  }

  def gte(gte: Any): GeoDistanceRangeQueryDefinition = {
    builder.gte(gte)
    this
  }

  def lte(lte: Any): GeoDistanceRangeQueryDefinition = {
    builder.lte(lte)
    this
  }

  def includeLower(includeLower: Boolean): GeoDistanceRangeQueryDefinition = {
    builder.includeLower(includeLower)
    this
  }

  def includeUpper(includeUpper: Boolean): GeoDistanceRangeQueryDefinition = {
    builder.includeUpper(includeUpper)
    this
  }

  def queryName(name: String): GeoDistanceRangeQueryDefinition = {
    builder.queryName(name)
    this
  }
}
