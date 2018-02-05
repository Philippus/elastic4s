package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.http.search.RawQueryBodyFn
import com.sksamuel.elastic4s.http.search.queries.compound.{BoolQueryBuilderFn, BoostingQueryBodyFn, ConstantScoreBodyFn, DisMaxQueryBodyFn}
import com.sksamuel.elastic4s.http.search.queries.geo.{GeoBoundingBoxQueryBodyFn, GeoDistanceQueryBodyFn, GeoPolyonQueryBodyFn, GeoShapeQueryBodyFn}
import com.sksamuel.elastic4s.http.search.queries.nested.{HasChildBodyFn, HasParentBodyFn, NestedQueryBodyFn, ParentIdQueryBodyFn}
import com.sksamuel.elastic4s.http.search.queries.span._
import com.sksamuel.elastic4s.http.search.queries.specialized.{FunctionScoreQueryBuilderFn, MoreLikeThisBuilderFn, ScriptQueryBodyFn, ScriptScoreQueryBodyFn}
import com.sksamuel.elastic4s.http.search.queries.term._
import com.sksamuel.elastic4s.http.search.queries.text._
import com.sksamuel.elastic4s.json.{XContentBuilder, XContentFactory}
import com.sksamuel.elastic4s.searches.queries.funcscorer.{FunctionScoreQuery, ScriptScore}
import com.sksamuel.elastic4s.searches.queries.geo.{GeoBoundingBoxQuery, GeoDistanceQuery, GeoPolygonQuery, GeoShapeQuery}
import com.sksamuel.elastic4s.searches.queries.matches._
import com.sksamuel.elastic4s.searches.queries.span._
import com.sksamuel.elastic4s.searches.queries.term.{TermQuery, TermsLookupQuery, TermsQuery, TermsSetQuery}
import com.sksamuel.elastic4s.searches.queries.{IdQuery, _}

object QueryBuilderFn {
  def apply(q: Query): XContentBuilder = q match {
    case b: BoolQuery           => BoolQueryBuilderFn(b)
    case b: BoostingQuery       => BoostingQueryBodyFn(b)
    case q: CommonTermsQuery    => CommonTermsQueryBodyFn(q)
    case q: ConstantScore       => ConstantScoreBodyFn(q)
    case q: DisMaxQuery         => DisMaxQueryBodyFn(q)
    case q: ExistsQuery         => ExistsQueryBodyFn(q)
    case q: FunctionScoreQuery  => FunctionScoreQueryBuilderFn(q)
    case q: FuzzyQuery          => FuzzyQueryBodyFn(q)
    case q: GeoBoundingBoxQuery => GeoBoundingBoxQueryBodyFn(q)
    case q: GeoDistanceQuery    => GeoDistanceQueryBodyFn(q)
    case q: GeoPolygonQuery     => GeoPolyonQueryBodyFn(q)
    case q: GeoShapeQuery       => GeoShapeQueryBodyFn(q)
    case q: HasChildQuery       => HasChildBodyFn(q)
    case q: HasParentQuery      => HasParentBodyFn(q)
    case q: IdQuery             => IdQueryBodyFn(q)
    case q: MatchAllQuery       => MatchAllBodyFn(q)
    case q: MatchNoneQuery      => MatchNoneBodyFn(q)
    case q: MatchQuery          => MatchQueryBuilderFn(q)
    case q: MatchPhrase         => MatchPhraseQueryBodyFn(q)
    case q: MatchPhrasePrefix   => MatchPhrasePrefixBodyFn(q)
    case q: MoreLikeThisQuery   => MoreLikeThisBuilderFn(q)
    case q: MultiMatchQuery     => MultiMatchBodyFn(q)
    case q: NestedQuery         => NestedQueryBodyFn(q)
    case NoopQuery => XContentFactory.jsonBuilder()
    case q: ParentIdQuery       => ParentIdQueryBodyFn(q)
    case q: PrefixQuery         => PrefixQueryBodyFn(q)
    case q: QueryStringQuery    => QueryStringBodyFn(q)
    case r: RangeQuery          => RangeQueryBodyFn(r)
    case q: RawQuery            => RawQueryBodyFn(q)
    case q: RegexQuery          => RegexQueryBodyFn(q)
    case q: ScriptQuery         => ScriptQueryBodyFn(q)
    case q: ScriptScore         => ScriptScoreQueryBodyFn(q)
    case s: SimpleStringQuery   => SimpleStringBodyFn(s)
    case s: SpanContainingQuery => SpanContainingQueryBodyFn(s)
    case s: SpanFirstQuery      => SpanFirstQueryBodyFn(s)
    case s: SpanNearQuery       => SpanNearQueryBodyFn(s)
    case s: SpanMultiTermQuery  => SpanMultiTermQueryBodyFn(s)
    case s: SpanNotQuery        => SpanNotQueryBodyFn(s)
    case s: SpanOrQuery         => SpanOrQueryBodyFn(s)
    case s: SpanTermQuery       => SpanTermQueryBodyFn(s)
    case s: SpanWithinQuery     => SpanWithinQueryBodyFn(s)
    case t: TermQuery           => TermQueryBodyFn(t)
    case t: TermsQuery[_]       => TermsQueryBodyFn(t)
    case t: TermsLookupQuery    => TermsLookupQueryBodyFn(t)
    case t: TermsSetQuery       => TermsSetQueryBodyFn(t)
    case q: TypeQuery           => TypeQueryBodyFn(q)
    case q: WildcardQuery       => WildcardQueryBodyFn(q)

    // Not implemented
    case ni =>
      throw new NotImplementedError(s"Query ${ni.getClass.getName} has not yet been implemented for the HTTP client.")
  }
}
