package com.sksamuel.elastic4s.searches.queries.geo

import org.elasticsearch.common.geo.GeoPoint
import org.elasticsearch.index.query.{GeoDistanceRangeQueryBuilder, QueryBuilders}

object GeoDistanceRangeQueryBuilderFn {
  def apply(d: GeoDistanceRangeQueryDefinition): GeoDistanceRangeQueryBuilder = {
    val builder = QueryBuilders.geoDistanceRangeQuery(d.field, new GeoPoint(d.geopoint.lat, d.geopoint.getLon))
    d.geoDistance.foreach(builder.geoDistance)
    d.includeLower.foreach(builder.includeLower)
    d.includeUpper.foreach(builder.includeUpper)
    d.from.foreach {
      case number: Number => builder.from(number)
      case str: String => builder.from(str)
    }
    d.to.foreach {
      case number: Number => builder.to(number)
      case str: String => builder.to(str)
    }
    d.boost.foreach(builder.boost)
    d.queryName.foreach(builder.queryName)
    d.validationMethod.foreach(builder.setValidationMethod)
    d.ignoreUnmapped.foreach(builder.ignoreUnmapped)
    d.queryName.foreach(builder.to)
    builder
  }
}
