package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.searches.queries._
import com.sksamuel.elastic4s.searches.queries.`match`.{MatchAllQueryDefinition, MatchPhraseDefinition, MatchQueryDefinition}
import com.sksamuel.elastic4s.searches.queries.term.TermQueryDefinition
import org.elasticsearch.common.xcontent.XContentBuilder

object QueryBuilderFn {
  def apply(q: QueryDefinition): XContentBuilder = q match {
    case b: BoolQueryDefinition => BoolQueryBuilderFn(b)
    case q: CommonTermsQueryDefinition => CommonTermsQueryBodyFn(q)
    case q: MatchQueryDefinition => MatchBodyFn(q)
    case q: MatchAllQueryDefinition => MatchAllBodyFn(q)
    case q: MatchPhraseDefinition => MatchPhraseQueryBodyFn(q)
    case q: QueryStringQueryDefinition => QueryStringBodyFn(q)
    case s: SimpleStringQueryDefinition => SimpleStringBodyFn(s)
    case t: TermQueryDefinition => TermQueryBodyFn(t)
  }
}
