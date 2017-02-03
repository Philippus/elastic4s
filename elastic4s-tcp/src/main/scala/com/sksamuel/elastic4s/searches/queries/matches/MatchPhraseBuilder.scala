package com.sksamuel.elastic4s.searches.queries.matches

import org.elasticsearch.index.query.{MatchPhraseQueryBuilder, QueryBuilders}

object MatchPhraseBuilder {
  def apply(q: MatchPhraseDefinition): MatchPhraseQueryBuilder = {
    val builder = QueryBuilders.matchPhraseQuery(q.field, q.value)
    q.analyzer.foreach(builder.analyzer)
    q.boost.map(_.toFloat).foreach(builder.boost)
    q.queryName.foreach(builder.queryName)
    q.slop.foreach(builder.slop)
    builder
  }
}
