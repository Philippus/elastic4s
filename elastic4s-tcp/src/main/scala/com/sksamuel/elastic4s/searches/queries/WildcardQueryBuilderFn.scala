package com.sksamuel.elastic4s.searches.queries

import org.elasticsearch.index.query.{QueryBuilders, WildcardQueryBuilder}

object WildcardQueryBuilderFn {
  def apply(q: WildcardQueryDefinition): WildcardQueryBuilder = {
    val builder = QueryBuilders.wildcardQuery(q.field, q.query.toString)
    q.queryName.foreach(builder.queryName)
    q.boost.map(_.toFloat).foreach(builder.boost)
    q.rewrite.foreach(builder.rewrite)
    builder
  }
}
