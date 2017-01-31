package com.sksamuel.elastic4s.searches.queries.geo

import org.elasticsearch.index.query.{GeohashCellQuery, QueryBuilders}

object GeoHashCellQueryBuilderFn {
  def apply(d: GeoHashCellQueryDefinition): GeohashCellQuery.Builder = {
    val builder = QueryBuilders.geoHashCellQuery(d.field, d.geohash)
    d.precisionLevels.foreach(builder.precision)
    d.precisionString.foreach(builder.precision)
    d.neighbors.foreach(builder.neighbors)
    d.ignoreUnmapped.foreach(builder.ignoreUnmapped)
    d.boost.map(_.toFloat).foreach(builder.boost)
    d.queryName.foreach(builder.queryName)
    builder
  }
}
