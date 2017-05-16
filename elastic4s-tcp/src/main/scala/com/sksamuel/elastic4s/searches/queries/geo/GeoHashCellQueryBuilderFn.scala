package com.sksamuel.elastic4s.searches.queries.geo

import com.sksamuel.elastic4s.EnumConversions
import org.elasticsearch.index.query.{GeohashCellQuery, QueryBuilders}

object GeoHashCellQueryBuilderFn {

  import EnumConversions._

  def apply(d: GeoHashCellQueryDefinition): GeohashCellQuery.Builder = {
    val builder = d.geohash match {
      case Some(hash) => QueryBuilders.geoHashCellQuery(d.field, hash)
      case _ => QueryBuilders.geoHashCellQuery(d.field, d.geopoint.get)
    }
    d.precisionLevels.foreach(builder.precision)
    d.precisionString.foreach(builder.precision)
    d.neighbors.foreach(builder.neighbors)
    d.ignoreUnmapped.foreach(builder.ignoreUnmapped)
    d.boost.map(_.toFloat).foreach(builder.boost)
    d.queryName.foreach(builder.queryName)
    builder
  }
}
