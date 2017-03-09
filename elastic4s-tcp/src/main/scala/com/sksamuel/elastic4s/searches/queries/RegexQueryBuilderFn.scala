package com.sksamuel.elastic4s.searches.queries

import org.elasticsearch.index.query.{QueryBuilders, RegexpQueryBuilder}

object RegexQueryBuilderFn {
  def apply(q: RegexQueryDefinition): RegexpQueryBuilder = {
    val builder = QueryBuilders.regexpQuery(q.field, q.regex)
    if (q.flags.nonEmpty)
      builder.flags(q.flags: _*)
    q.queryName.foreach(builder.queryName)
    q.boost.map(_.toFloat).foreach(builder.boost)
    q.rewrite.foreach(builder.rewrite)
    builder
  }
}
