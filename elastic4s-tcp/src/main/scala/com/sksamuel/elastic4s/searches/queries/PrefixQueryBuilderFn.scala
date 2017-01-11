package com.sksamuel.elastic4s.searches.queries

import org.elasticsearch.index.query.{PrefixQueryBuilder, QueryBuilders}

object PrefixQueryBuilderFn {
  def apply(q: PrefixQueryDefinition): PrefixQueryBuilder = {
    val builder = QueryBuilders.prefixQuery(q.field, q.prefix.toString)
    q.queryName.foreach(builder.queryName)
    q.boost.map(_.toFloat).foreach(builder.boost)
    q.rewrite.foreach(builder.rewrite)
    builder
  }
}
