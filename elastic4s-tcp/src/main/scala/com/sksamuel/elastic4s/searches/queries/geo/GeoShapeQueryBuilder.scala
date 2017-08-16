//package com.sksamuel.elastic4s.searches.queries.geo
//
//import org.elasticsearch.index.query.GeoShapeQueryBuilder
//
//object GeoShapeQueryBuilder {
//  def apply(q: GeoShapeDefinition): GeoShapeQueryBuilder = {
//    val _builder = q.builder
//    q.ignoreUnmapped.foreach(_builder.ignoreUnmapped)
//    q.indexedShapeIndex.foreach(_builder.indexedShapeIndex)
//    q.indexedShapePath.foreach(_builder.indexedShapePath)
//    q.relation.foreach(_builder.relation)
//    q.strategy.foreach(_builder.strategy)
//    q.boost.foreach(_builder.boost)
//    q.queryName.foreach(_builder.queryName)
//    _builder
//  }
//}
