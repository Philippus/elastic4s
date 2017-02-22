package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.searches.queries._
import com.sksamuel.elastic4s.searches.queries.matches._
import com.sksamuel.elastic4s.searches.queries.funcscorer.{FunctionScoreBuilderFn, FunctionScoreQueryDefinition}
import com.sksamuel.elastic4s.searches.queries.geo._
import com.sksamuel.elastic4s.searches.queries.span._
import com.sksamuel.elastic4s.searches.queries.term.{TermQueryDefinition, TermsLookupQueryDefinition, TermsQueryDefinition}
import org.elasticsearch.index.query.{QueryBuilder, QueryBuilders}

object QueryBuilderFn {
  def apply(query: QueryDefinition): QueryBuilder = query match {
    case q: BoolQueryDefinition => BoolQueryBuilder(q)
    case q: BoostingQueryDefinition => BoostingQueryBuilder(q)
    case q: ConstantScoreDefinition => ConstantScoreBuilder(q)
    case q: CommonTermsQueryDefinition => CommonTermsQueryBuilder(q)
    case q: DisMaxQueryDefinition => DisMaxBuilder(q)
    case q: ExistsQueryDefinition => ExistsQueryBuilder(q)
    case q: FunctionScoreQueryDefinition => FunctionScoreBuilderFn(q)
    case q: FuzzyQueryDefinition => FuzzyQueryBuilder(q)
    case q: GeoShapeDefinition => GeoShapeQueryBuilder(q)
    case q: GeoPolygonQueryDefinition => GeoPolygonQueryBuilder(q)
    case q: GeoDistanceQueryDefinition => GeoDistanceQueryBuilder(q)
    case q: GeoBoundingBoxQueryDefinition => GeoBoundingBoxQueryBuilder(q)
    case q: GeoDistanceRangeQueryDefinition => GeoDistanceRangeQueryBuilderFn(q)
    case q: HasChildQueryDefinition => HasChildQueryBuilder(q)
    case q: HasParentQueryDefinition => HasParentQueryBuilder(q)
    case q: IdQueryDefinition => IdQueryBuilder(q)
    case q: MatchAllQueryDefinition => MatchAllQueryBuilder(q)
    case q: MatchQueryDefinition => MatchQueryBuilder(q)
    case q: MatchPhraseDefinition => MatchPhraseBuilder(q)
    case q: MatchPhrasePrefixDefinition => MatchPhrasePrefixBuilder(q)
    case q: MoreLikeThisQueryDefinition => MoreLikeThisQueryBuilderFn(q)
    case q: MultiMatchQueryDefinition => MultiMatchQueryBuilderFn(q)
    case q: NestedQueryDefinition => NestedQueryBuilder(q)
    case q: QueryStringQueryDefinition => QueryStringBuilder.builder(q)
    case q: PercolateQueryDefinition => PercolateQueryBuilder(q)
    case q: PrefixQueryDefinition => PrefixQueryBuilderFn(q)
    case q: RegexQueryDefinition => RegexQueryBuilder(q)
    case q: RangeQueryDefinition => RangeQueryBuilder(q)
    case q: ScriptQueryDefinition => ScriptQueryBuilder(q)
    case q: SimpleStringQueryDefinition => SimpleStringQueryBuilder(q)
    case q: SpanTermQueryDefinition => SpanTermQueryBuilder(q)
    case q: SpanFirstQueryDefinition => SpanFirstQueryBuilder(q)
    case q: SpanNearQueryDefinition => SpanNearQueryBuilder(q)
    case q: SpanMultiTermQueryDefinition => SpanMultiTermQueryBuilder(q)
    case q: SpanNotQueryDefinition => SpanNotQueryBuilder(q)
    case q: SpanOrQueryDefinition => SpanOrQueryBuilder(q)
    case q: TermQueryDefinition => TermQueryBuilder(q)
    case q: TermsQueryDefinition[_] => TermsQueryBuilder(q)
    case q: TermsLookupQueryDefinition => TermsLookupQueryBuilder(q)
    case q: TypeQueryDefinition => QueryBuilders.typeQuery(q.`type`)
    case q: WildcardQueryDefinition => WildcardQueryBuilder(q)
  }
}
