package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.searches.queries.`match`.{MatchAllQueryDefinition, MatchQueryDefinition}
import com.sksamuel.elastic4s.searches.queries.term.TermQueryDefinition
import com.sksamuel.elastic4s.searches.queries.{CommonTermsQueryDefinition, QueryDefinition, QueryStringQueryDefinition, SimpleStringQueryDefinition}
import org.elasticsearch.common.xcontent.XContentBuilder

object QueryBuilderFn {
  def apply(q: QueryDefinition): XContentBuilder = q match {
    case q: CommonTermsQueryDefinition => CommonTermsQueryBodyFn(q)
    case q: MatchQueryDefinition => MatchBodyFn(q)
    case q: MatchAllQueryDefinition => MatchAllBodyFn(q)
    case q: QueryStringQueryDefinition => QueryStringBodyFn(q)
    case s: SimpleStringQueryDefinition => SimpleStringBodyFn(s)
    case t: TermQueryDefinition => TermQueryBodyFn(t)
  }
}
