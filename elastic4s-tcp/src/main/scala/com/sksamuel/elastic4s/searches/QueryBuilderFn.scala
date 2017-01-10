package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.searches.queries._
import org.elasticsearch.index.query.QueryBuilder

object QueryBuilderFn {
  def apply(query: QueryDefinition): QueryBuilder = query match {
    case q: QueryStringQueryDefinition => QueryStringBuilder.builder(q)
    case q: MatchAllQueryDefinition => MatchAllQueryBuilder(q)
    case q: MatchQueryDefinition => MatchQueryBuilder(q)
    case q: IdQueryDefinition => IdQueryBuilder(q)
    case q: TermQueryDefinition => TermQueryBuilder(q)
  }
}


