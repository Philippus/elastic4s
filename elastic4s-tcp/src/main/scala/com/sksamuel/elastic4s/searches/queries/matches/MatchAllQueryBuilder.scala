package com.sksamuel.elastic4s.searches.queries.matches

import org.elasticsearch.index.query.{MatchAllQueryBuilder, MatchNoneQueryBuilder, QueryBuilders}

object MatchAllQueryBuilder {
  def apply(q: MatchAllQueryDefinition): MatchAllQueryBuilder = {
    val builder = QueryBuilders.matchAllQuery
    q.boost.foreach(builder.boost)
    q.queryName.foreach(builder.queryName)
    builder
  }
}


object MatchNoneQueryBuilder {
  def apply(q: MatchNoneQueryDefinition): MatchNoneQueryBuilder = {
    val builder = new MatchNoneQueryBuilder()
    q.queryName.foreach(builder.queryName)
    builder
  }
}
