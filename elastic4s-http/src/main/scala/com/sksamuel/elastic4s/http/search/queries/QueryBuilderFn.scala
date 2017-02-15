package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.searches.queries.matches.{MatchAllQueryDefinition, MatchPhraseDefinition, MatchQueryDefinition, MultiMatchQueryDefinition}
import com.sksamuel.elastic4s.searches.queries.term.{TermQueryDefinition, TermsQueryDefinition}
import com.sksamuel.elastic4s.searches.queries.{IdQueryDefinition, _}
import org.elasticsearch.common.xcontent.XContentBuilder

object QueryBuilderFn {
  def apply(q: QueryDefinition): XContentBuilder = q match {
    case b: BoolQueryDefinition => BoolQueryBuilderFn(b)
    case b: BoostingQueryDefinition => BoostingQueryBodyFn(b)
    case q: CommonTermsQueryDefinition => CommonTermsQueryBodyFn(q)
    case q: ConstantScoreDefinition => ConstantScoreBodyFn(q)
    case q: ExistsQueryDefinition => ExistsQueryBodyFn(q)
    case q: IdQueryDefinition => IdQueryBodyFn(q)
    case q: MatchQueryDefinition => MatchBodyFn(q)
    case q: MatchAllQueryDefinition => MatchAllBodyFn(q)
    case q: MatchPhraseDefinition => MatchPhraseQueryBodyFn(q)
    case q: MultiMatchQueryDefinition => MultiMatchBodyFn(q)
    case q: QueryStringQueryDefinition => QueryStringBodyFn(q)
    case q: PrefixQueryDefinition => PrefixQueryBodyFn(q)
    case q: RegexQueryDefinition => RegexQueryBodyFn(q)
    case s: SimpleStringQueryDefinition => SimpleStringBodyFn(s)
    case t: TermQueryDefinition => TermQueryBodyFn(t)
    case t: TermsQueryDefinition[_] => TermsQueryBodyFn(t)
    case q: TypeQueryDefinition => TypeQueryBodyFn(q)
    case q: WildcardQueryDefinition => WildcardQueryBodyFn(q)
  }
}
