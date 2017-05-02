package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.searches.queries._
import com.sksamuel.elastic4s.searches.queries.funcscorer.{FunctionScoreBuilderFn, FunctionScoreQueryDefinition}
import com.sksamuel.elastic4s.searches.queries.geo._
import com.sksamuel.elastic4s.searches.queries.matches._
import com.sksamuel.elastic4s.searches.queries.span._
import com.sksamuel.elastic4s.searches.queries.term.{TermQueryDefinition, TermsLookupQueryDefinition, TermsQueryDefinition}
import org.elasticsearch.index.query.{QueryBuilder, QueryBuilders}

object QueryBuilderFn {
  def apply(query: QueryDefinition): QueryBuilder = query match {
    case q: BoolQueryDefinition => BoolQueryBuilderFn(q)
    case q: BoostingQueryDefinition => BoostingQueryBuilderFn(q)
    case q: CommonTermsQueryDefinition => CommonTermsQueryBuilderFn(q)
    case q: ConstantScoreDefinition => ConstantScoreBuilderF(q)
    case q: DisMaxQueryDefinition => DisMaxBuilderFn(q)
    case q: ExistsQueryDefinition => ExistsQueryBuilderFn(q)
    case q: FunctionScoreQueryDefinition => FunctionScoreBuilderFn(q)
    case q: FuzzyQueryDefinition => FuzzyQueryBuilderFn(q)
    case q: GeoBoundingBoxQueryDefinition => GeoBoundingBoxQueryBuilder(q)
    case q: GeoDistanceQueryDefinition => GeoDistanceQueryBuilder(q)
    case q: GeoDistanceRangeQueryDefinition => GeoDistanceRangeQueryBuilderFn(q)
    case q: GeoPolygonQueryDefinition => GeoPolygonQueryBuilderFn(q)
    case q: GeoShapeDefinition => GeoShapeQueryBuilder(q)
    case q: HasChildQueryDefinition => HasChildQueryBuilderFn(q)
    case q: HasParentQueryDefinition => HasParentQueryBuilderFn(q)
    case q: IdQueryDefinition => IdQueryBuilderFn(q)
    case q: MatchAllQueryDefinition => MatchAllQueryBuilder(q)
    case q: MatchNoneQueryDefinition => MatchNoneQueryBuilder(q)
    case q: MatchQueryDefinition => MatchQueryBuilder(q)
    case q: MatchPhraseDefinition => MatchPhraseBuilder(q)
    case q: MatchPhrasePrefixDefinition => MatchPhrasePrefixBuilder(q)
    case q: MoreLikeThisQueryDefinition => MoreLikeThisQueryBuilderFn(q)
    case q: MultiMatchQueryDefinition => MultiMatchQueryBuilderFn(q)
    case q: NestedQueryDefinition => NestedQueryBuilderFn(q)
    case q: PercolateQueryDefinition => PercolateQueryBuilderFn(q)
    case q: PrefixQueryDefinition => PrefixQueryBuilderFn(q)
    case q: QueryStringQueryDefinition => QueryStringBuilderFn.builder(q)
    case q: RawQueryDefinition => RawQueryBuilderFn(q)
    case q: RegexQueryDefinition => RegexQueryBuilderFn(q)
    case q: RangeQueryDefinition => RangeQueryBuilderFn(q)
    case q: ScriptQueryDefinition => ScriptQueryBuilderFn(q)
    case q: SimpleStringQueryDefinition => SimpleStringQueryBuilderFn(q)
    case q: SpanContainingQueryDefinition => SpanContainingQueryBuilder(q)
    case q: SpanFirstQueryDefinition => SpanFirstQueryBuilder(q)
    case q: SpanMultiTermQueryDefinition => SpanMultiTermQueryBuilder(q)
    case q: SpanNearQueryDefinition => SpanNearQueryBuilder(q)
    case q: SpanNotQueryDefinition => SpanNotQueryBuilder(q)
    case q: SpanOrQueryDefinition => SpanOrQueryBuilder(q)
    case q: SpanTermQueryDefinition => SpanTermQueryBuilder(q)
    case q: SpanWithinQueryDefinition => SpanWithinQueryBuilder(q)
    case q: TermQueryDefinition => TermQueryBuilderFn(q)
    case q: TermsQueryDefinition[_] => TermsQueryBuilderFn(q)
    case q: TermsLookupQueryDefinition => TermsLookupQueryBuilderFn(q)
    case q: TypeQueryDefinition => QueryBuilders.typeQuery(q.`type`)
    case q: WildcardQueryDefinition => WildcardQueryBuilderFn(q)
  }
}
