package com.sksamuel.elastic4s.searches.queries

import org.elasticsearch.index.query.{QueryBuilders, RangeQueryBuilder}

object RangeQueryBuilderFn {
  def apply(q: RangeQueryDefinition): RangeQueryBuilder = {
    val builder = QueryBuilders.rangeQuery(q.field)
    q.queryName.foreach(builder.queryName)
    q.boost.map(_.toFloat).foreach(builder.boost)
    q.from.foreach(builder.from)
    q.to.foreach(builder.to)
    q.gte.foreach(builder.gte)
    q.lte.foreach(builder.lte)
    q.includeLower.foreach(builder.includeLower)
    q.includeUpper.foreach(builder.includeUpper)
    q.timeZone.foreach(builder.timeZone)
    builder
  }
}
