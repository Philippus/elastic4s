package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.searches.QueryBuilderFn
import org.elasticsearch.index.query.{DisMaxQueryBuilder, QueryBuilders}

object DisMaxBuilder {
  def apply(q: DisMaxQueryDefinition): DisMaxQueryBuilder = {
    val builder = QueryBuilders.disMaxQuery()
    q.queries.foreach(q => builder.add(QueryBuilderFn(q)))
    q.boost.map(_.toFloat).foreach(builder.boost)
    q.tieBreaker.map(_.toFloat).foreach(builder.tieBreaker)
    q.queryName.foreach(builder.queryName)
    builder
  }
}
