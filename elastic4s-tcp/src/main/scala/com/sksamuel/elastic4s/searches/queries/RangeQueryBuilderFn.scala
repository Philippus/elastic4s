package com.sksamuel.elastic4s.searches.queries

import org.elasticsearch.index.query.{QueryBuilders, RangeQueryBuilder}

object RangeQueryBuilderFn {
  def apply(q: RangeQuery): RangeQueryBuilder = {
    val builder = QueryBuilders.rangeQuery(q.field)
    q.queryName.foreach(builder.queryName)
    q.boost.map(_.toFloat).foreach(builder.boost)
    q.gt.foreach(builder.gt)
    q.lt.foreach(builder.lt)
    q.gte.foreach(builder.gte)
    q.lte.foreach(builder.lte)
    q.format.foreach(builder.format)
    q.timeZone.foreach(builder.timeZone)
    builder
  }
}
