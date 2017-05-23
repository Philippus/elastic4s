package com.sksamuel.elastic4s.http.search.queries

import com.sksamuel.elastic4s.http.search.RawQueryBodyFn
import com.sksamuel.elastic4s.http.search.queries.compound.{BoolQueryBuilderFn, BoostingQueryBodyFn, ConstantScoreBodyFn, DisMaxQueryBodyFn}
import com.sksamuel.elastic4s.http.search.queries.geo.{GeoBoundingBoxQueryBodyFn, GeoDistanceQueryBodyFn, GeoPolyonQueryBodyFn}
import com.sksamuel.elastic4s.http.search.queries.nested.{HasChildBodyFn, HasParentBodyFn, NestedQueryBodyFn, ParentIdQueryBodyFn}
import com.sksamuel.elastic4s.http.search.queries.span._
import com.sksamuel.elastic4s.http.search.queries.specialized.{FunctionScoreQueryBodyFn, MoreLikeThisBuilderFn, ScriptQueryBodyFn, ScriptScoreQueryBodyFn}
import com.sksamuel.elastic4s.http.search.queries.term._
import com.sksamuel.elastic4s.http.search.queries.text._
import com.sksamuel.elastic4s.searches.queries.funcscorer.{FunctionScoreQueryDefinition, ScriptScoreDefinition}
import com.sksamuel.elastic4s.searches.queries.geo.{GeoBoundingBoxQueryDefinition, GeoDistanceQueryDefinition, GeoPolygonQueryDefinition}
import com.sksamuel.elastic4s.searches.queries.matches._
import com.sksamuel.elastic4s.searches.queries.span._
import com.sksamuel.elastic4s.searches.queries.term.{TermQueryDefinition, TermsQueryDefinition}
import com.sksamuel.elastic4s.searches.queries.{IdQueryDefinition, _}
import org.elasticsearch.common.xcontent.XContentBuilder

object QueryBuilderFn {
  def apply(q: QueryDefinition): XContentBuilder = q match {
    case b: BoolQueryDefinition => BoolQueryBuilderFn(b)
    case b: BoostingQueryDefinition => BoostingQueryBodyFn(b)
    case q: CommonTermsQueryDefinition => CommonTermsQueryBodyFn(q)
    case q: ConstantScoreDefinition => ConstantScoreBodyFn(q)
    case q: DisMaxQueryDefinition => DisMaxQueryBodyFn(q)
    case q: ExistsQueryDefinition => ExistsQueryBodyFn(q)
    case q: FuzzyQueryDefinition => FuzzyQueryBodyFn(q)
    case q: GeoBoundingBoxQueryDefinition => GeoBoundingBoxQueryBodyFn(q)
    case q: GeoDistanceQueryDefinition => GeoDistanceQueryBodyFn(q)
    case q: GeoPolygonQueryDefinition => GeoPolyonQueryBodyFn(q)
    case q: HasChildQueryDefinition => HasChildBodyFn(q)
    case q: HasParentQueryDefinition => HasParentBodyFn(q)
    case q: IdQueryDefinition => IdQueryBodyFn(q)
    case q: MatchAllQueryDefinition => MatchAllBodyFn(q)
    case q: MatchNoneQueryDefinition => MatchNoneBodyFn(q)
    case q: MatchQueryDefinition => MatchBodyFn(q)
    case q: MatchPhraseDefinition => MatchPhraseQueryBodyFn(q)
    case q: MatchPhrasePrefixDefinition => MatchPhrasePrefixBodyFn(q)
    case q: MoreLikeThisQueryDefinition => MoreLikeThisBuilderFn(q)
    case q: MultiMatchQueryDefinition => MultiMatchBodyFn(q)
    case q: NestedQueryDefinition => NestedQueryBodyFn(q)
    case q: ParentIdQueryDefinition => ParentIdQueryBodyFn(q)
    case q: PrefixQueryDefinition => PrefixQueryBodyFn(q)
    case q: QueryStringQueryDefinition => QueryStringBodyFn(q)
    case r: RangeQueryDefinition => RangeQueryBodyFn(r)
    case q: RawQueryDefinition => RawQueryBodyFn(q)
    case q: RegexQueryDefinition => RegexQueryBodyFn(q)
    case q: ScriptQueryDefinition => ScriptQueryBodyFn(q)
    case s: SimpleStringQueryDefinition => SimpleStringBodyFn(s)
    case s: SpanContainingQueryDefinition => SpanContainingQueryBodyFn(s)
    case s: SpanFirstQueryDefinition => SpanFirstQueryBodyFn(s)
    case s: SpanNearQueryDefinition => SpanNearQueryBodyFn(s)
    case s: SpanMultiTermQueryDefinition => SpanMultiTermQueryBodyFn(s)
    case s: SpanNotQueryDefinition => SpanNotQueryBodyFn(s)
    case s: SpanOrQueryDefinition => SpanOrQueryBodyFn(s)
    case s: SpanTermQueryDefinition => SpanTermQueryBodyFn(s)
    case s: SpanWithinQueryDefinition => SpanWithinQueryBodyFn(s)
    case t: TermQueryDefinition => TermQueryBodyFn(t)
    case t: TermsQueryDefinition[_] => TermsQueryBodyFn(t)
    case q: TypeQueryDefinition => TypeQueryBodyFn(q)
    case q: WildcardQueryDefinition => WildcardQueryBodyFn(q)
    case q: FunctionScoreQueryDefinition => FunctionScoreQueryBodyFn(q)
    case q: ScriptScoreDefinition => ScriptScoreQueryBodyFn(q)
  }
}
