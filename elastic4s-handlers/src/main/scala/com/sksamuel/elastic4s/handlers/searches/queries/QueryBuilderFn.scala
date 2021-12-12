package com.sksamuel.elastic4s.handlers.searches.queries

import com.sksamuel.elastic4s.handlers.searches.RawQueryBodyFn
import com.sksamuel.elastic4s.handlers.searches.queries.compound.{BoolQueryBuilderFn, BoostingQueryBodyFn, ConstantScoreBodyFn, DisMaxQueryBodyFn}
import com.sksamuel.elastic4s.handlers.searches.queries.geo.{GeoBoundingBoxQueryBodyFn, GeoDistanceQueryBodyFn, GeoPolyonQueryBodyFn, GeoShapeQueryBodyFn}
import com.sksamuel.elastic4s.handlers.searches.queries.nested.{HasChildBodyFn, HasParentBodyFn, NestedQueryBodyFn, ParentIdQueryBodyFn}
import com.sksamuel.elastic4s.handlers.searches.queries.span.{SpanContainingQueryBodyFn, SpanFieldMaskingQueryBodyFn, SpanFirstQueryBodyFn, SpanMultiTermQueryBodyFn, SpanNearQueryBodyFn, SpanNotQueryBodyFn, SpanOrQueryBodyFn, SpanTermQueryBodyFn, SpanWithinQueryBodyFn}
import com.sksamuel.elastic4s.handlers.searches.queries.term.{ExistsQueryBodyFn, FuzzyQueryBodyFn, IdQueryBodyFn, PrefixQueryBodyFn, RangeQueryBodyFn, RegexQueryBodyFn, TermQueryBodyFn, TermsLookupQueryBodyFn, TermsQueryBodyFn, TermsSetQueryBodyFn, WildcardQueryBodyFn}
import com.sksamuel.elastic4s.handlers.searches.queries.text.{CombinedFieldsQueryBodyFn, CommonTermsQueryBodyFn, MatchAllBodyFn, MatchBoolPrefixBodyFn, MatchNoneBodyFn, MatchPhrasePrefixBodyFn, MatchPhraseQueryBodyFn, MatchQueryBuilderFn, MultiMatchBodyFn, QueryStringBodyFn, SimpleStringBodyFn}
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.requests.searches.queries.compound.BoolQuery
import com.sksamuel.elastic4s.requests.searches.queries.funcscorer.{FunctionScoreQuery, ScriptScore}
import com.sksamuel.elastic4s.requests.searches.queries.geo.{GeoBoundingBoxQuery, GeoDistanceQuery, GeoPolygonQuery, GeoShapeQuery}
import com.sksamuel.elastic4s.requests.searches.queries.matches.{MatchAllQuery, MatchBoolPrefixQuery, MatchNoneQuery, MatchPhrasePrefixQuery, MatchPhraseQuery, MatchQuery, MultiMatchQuery}
import com.sksamuel.elastic4s.requests.searches.queries.{BoostingQuery, CombinedFieldsQuery, CommonTermsQuery, ConstantScore, DisMaxQuery, DistanceFeatureQuery, ExistsQuery, FuzzyQuery, HasChildQuery, HasParentQuery, IdQuery, IntervalsQuery, MoreLikeThisQuery, NestedQuery, NoopQuery, ParentIdQuery, PercolateQuery, PinnedQuery, PrefixQuery, Query, QueryStringQuery, RangeQuery, RankFeatureQuery, RawQuery, RegexQuery, ScriptQuery, SimpleStringQuery}
import com.sksamuel.elastic4s.requests.searches.span.{SpanContainingQuery, SpanFieldMaskingQuery, SpanFirstQuery, SpanMultiTermQuery, SpanNearQuery, SpanNotQuery, SpanOrQuery, SpanTermQuery, SpanWithinQuery}
import com.sksamuel.elastic4s.requests.searches.term.{TermQuery, TermsLookupQuery, TermsQuery, TermsSetQuery, WildcardQuery}

object QueryBuilderFn {
  def apply(q: Query): XContentBuilder = q match {
    case b: BoolQuery => BoolQueryBuilderFn(b)
    case b: BoostingQuery => BoostingQueryBodyFn(b)
    case q: CombinedFieldsQuery => CombinedFieldsQueryBodyFn(q)
    case q: CommonTermsQuery => CommonTermsQueryBodyFn(q)
    case q: ConstantScore => ConstantScoreBodyFn(q)
    case q: DistanceFeatureQuery => DistanceFeatureQueryBuilderFn(q)
    case q: DisMaxQuery => DisMaxQueryBodyFn(q)
    case q: ExistsQuery => ExistsQueryBodyFn(q)
    case q: FunctionScoreQuery => FunctionScoreQueryBuilderFn(q)
    case q: FuzzyQuery => FuzzyQueryBodyFn(q)
    case q: GeoBoundingBoxQuery => GeoBoundingBoxQueryBodyFn(q)
    case q: GeoDistanceQuery => GeoDistanceQueryBodyFn(q)
    case q: GeoPolygonQuery => GeoPolyonQueryBodyFn(q)
    case q: GeoShapeQuery => GeoShapeQueryBodyFn(q)
    case q: HasChildQuery => HasChildBodyFn(q)
    case q: HasParentQuery => HasParentBodyFn(q)
    case q: IdQuery => IdQueryBodyFn(q)
    case q: IntervalsQuery => IntervalsQueryBuilderFn(q)
    case q: MatchAllQuery => MatchAllBodyFn(q)
    case q: MatchNoneQuery => MatchNoneBodyFn(q)
    case q: MatchQuery => MatchQueryBuilderFn(q)
    case q: MatchPhraseQuery => MatchPhraseQueryBodyFn(q)
    case q: MatchPhrasePrefixQuery => MatchPhrasePrefixBodyFn(q)
    case q: MatchBoolPrefixQuery => MatchBoolPrefixBodyFn(q)
    case q: MoreLikeThisQuery => MoreLikeThisQueryBuilderFn(q)
    case q: MultiMatchQuery => MultiMatchBodyFn(q)
    case q: NestedQuery => NestedQueryBodyFn(q)
    case NoopQuery => XContentFactory.jsonBuilder()
    case q: ParentIdQuery => ParentIdQueryBodyFn(q)
    case q: PercolateQuery => PercolateQueryBodyFn(q)
    case q: PinnedQuery => PinnedQueryBuilderFn(q)
    case q: PrefixQuery => PrefixQueryBodyFn(q)
    case q: QueryStringQuery => QueryStringBodyFn(q)
    case r: RangeQuery => RangeQueryBodyFn(r)
    case r: RankFeatureQuery => RankFeatureQueryBuilderFn(r)
    case q: RawQuery => RawQueryBodyFn(q)
    case q: RegexQuery => RegexQueryBodyFn(q)
    case q: ScriptQuery => ScriptQueryBodyFn(q)
    case q: ScriptScore => ScriptScoreQueryBodyFn(q)
    case s: SimpleStringQuery => SimpleStringBodyFn(s)
    case s: SpanContainingQuery => SpanContainingQueryBodyFn(s)
    case s: SpanFirstQuery => SpanFirstQueryBodyFn(s)
    case s: SpanNearQuery => SpanNearQueryBodyFn(s)
    case s: SpanMultiTermQuery => SpanMultiTermQueryBodyFn(s)
    case s: SpanNotQuery => SpanNotQueryBodyFn(s)
    case s: SpanOrQuery => SpanOrQueryBodyFn(s)
    case s: SpanTermQuery => SpanTermQueryBodyFn(s)
    case s: SpanWithinQuery => SpanWithinQueryBodyFn(s)
    case t: TermQuery => TermQueryBodyFn(t)
    case t: TermsQuery[_] => TermsQueryBodyFn(t)
    case t: TermsLookupQuery => TermsLookupQueryBodyFn(t)
    case t: TermsSetQuery => TermsSetQueryBodyFn(t)
    case q: WildcardQuery => WildcardQueryBodyFn(q)
    case q: SpanFieldMaskingQuery => SpanFieldMaskingQueryBodyFn(q)
//    case c: CustomQuery => c.buildQueryBody()

    // Not implemented
    case ni =>
      throw new NotImplementedError(s"Query ${ni.getClass.getName} has not yet been implemented for the HTTP client.")
  }
}
