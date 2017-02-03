package com.sksamuel.elastic4s.searches.queries.matches

import org.elasticsearch.index.query.{MatchAllQueryBuilder, QueryBuilders}

object MatchAllQueryBuilder {
  def apply(q: MatchAllQueryDefinition): MatchAllQueryBuilder = {
    val builder = QueryBuilders.matchAllQuery
    q.boost.foreach(builder.boost)
    q.queryName.foreach(builder.queryName)
    builder
  }
}
