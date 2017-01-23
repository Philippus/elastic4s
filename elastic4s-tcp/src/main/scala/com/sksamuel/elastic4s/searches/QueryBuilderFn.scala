package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.searches.queries._
import com.sksamuel.elastic4s.searches.queries.`match`._
import com.sksamuel.elastic4s.searches.queries.funcscorer.FunctionScoreQueryDefinition
import com.sksamuel.elastic4s.searches.queries.geo._
import com.sksamuel.elastic4s.searches.queries.span._
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder
import org.elasticsearch.index.query.{QueryBuilder, QueryBuilders}

object QueryBuilderFn {
  def apply(query: QueryDefinition): QueryBuilder = query match {
    case q: QueryStringQueryDefinition => QueryStringBuilder.builder(q)
    case q: MatchAllQueryDefinition => MatchAllQueryBuilder(q)
    case q: MatchQueryDefinition => MatchQueryBuilder(q)
    case q: IdQueryDefinition => IdQueryBuilder(q)
    case q: TermQueryDefinition => TermQueryBuilder(q)
    case q: PrefixQueryDefinition => PrefixQueryBuilderFn(q)
    case q: WildcardQueryDefinition => WildcardQueryBuilder(q)
    case q: ExistsQueryDefinition => ExistsQueryBuilder(q)
    case q: MatchPhraseDefinition => MatchPhraseBuilder(q)
    case q: BoostingQueryDefinition => BoostingQueryBuilder(q)
    case q: DisMaxDefinition => DisMaxBuilder(q)
    case q: FunctionScoreQueryDefinition => FunctionScoreBuilderFn(q)
    case q: FuzzyQueryDefinition => FuzzyQueryBuilder(q)
    case q: HasChildQueryDefinition => HasChildQueryBuilder(q)
    case q: HasParentQueryDefinition => HasParentQueryBuilder(q)
    case q: GeoShapeDefinition => GeoShapeQueryBuilder(q)
    case q: SimpleStringQueryDefinition => SimpleStringQueryBuilder(q)
    case q: RegexQueryDefinition => RegexQueryBuilder(q)
    case q: RangeQueryDefinition => RangeQueryBuilder(q)
    case q: GeoPolygonQueryDefinition => GeoPolygonQueryBuilder(q)
    case q: TermsQueryDefinition[_] => TermsQueryBuilder(q)
    case q: ScriptQueryDefinition => ScriptQueryBuilder(q)
    case q: BoolQueryDefinition => BoolQueryBuilder(q)
    case q: MatchPhrasePrefixDefinition => MatchPhrasePrefixBuilder(q)
    case q: TypeQueryDefinition => QueryBuilders.typeQuery(q.`type`)
    case q: MultiMatchQueryDefinition => MultiMatchQueryBuilderFn(q)
    case q: ConstantScoreDefinition => ConstantScoreBuilder(q)
    case q: CommonTermsQueryDefinition => CommonTermsQueryBuilder(q)
    case q: SpanTermQueryDefinition => SpanTermQueryBuilder(q)
    case q: SpanFirstQueryDefinition => SpanFirstQueryBuilder(q)
    case q: SpanNearQueryDefinition => SpanNearQueryBuilder(q)
    case q: SpanMultiTermQueryDefinition => SpanMultiTermQueryBuilder(q)
    case q: SpanNotQueryDefinition => SpanNotQueryBuilder(q)
    case q: SpanOrQueryDefinition => SpanOrQueryBuilder(q)
    case q: MoreLikeThisQueryDefinition => MoreLikeThisQueryBuilderFn(q)
    case q: GeoDistanceQueryDefinition => GeoDistanceQueryBuilder(q)
    case q: NestedQueryDefinition => NestedQueryBuilder(q)
    case q: GeoBoundingBoxQueryDefinition => GeoBoundingBoxQueryBuilder(q)
  }
}

object FunctionScoreBuilderFn {
  def apply(q: FunctionScoreQueryDefinition): FunctionScoreQueryBuilder = {
    val builder = q.query match {
      case Some(query) => new FunctionScoreQueryBuilder(QueryBuilderFn(query), q.scorers.map(_.builder).toArray)
      case _ => new FunctionScoreQueryBuilder(q.scorers.map(_.builder).toArray)
    }

    q.boost.map(_.toFloat).foreach(builder.boost)
    q.maxBoost.map(_.toFloat).foreach(builder.maxBoost)
    q.minScore.map(_.toFloat).foreach(builder.setMinScore)
    q.boostMode.foreach(builder.boostMode)
    q.scoreMode.foreach(builder.scoreMode)
    builder
  }
}


