package com.sksamuel.elastic4s.searches.queries.`match`

import org.elasticsearch.index.query.{MatchPhrasePrefixQueryBuilder, QueryBuilders}

object MatchPhrasePrefixBuilder {
  def apply(q: MatchPhrasePrefixDefinition): MatchPhrasePrefixQueryBuilder = {
    val _builder = QueryBuilders.matchPhrasePrefixQuery(q.field, q.value.toString)
    q.queryName.foreach(_builder.queryName)
    q.boost.map(_.toFloat).foreach(_builder.boost)
    q.analyzer.foreach(_builder.analyzer)
    q.maxExpansions.foreach(_builder.maxExpansions)
    q.queryName.foreach(_builder.queryName)
    q.slop.foreach(_builder.slop)
    _builder
  }
}
