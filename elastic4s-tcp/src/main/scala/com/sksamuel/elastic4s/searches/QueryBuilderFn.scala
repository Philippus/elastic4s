package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.searches.queries._
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
    case q: FuzzyQueryDefinition => FuzzyQueryBuilder(q)
    case q: HasChildQueryDefinition => HasChildQueryBuilder(q)
    case q: HasParentQueryDefinition => HasParentQueryBuilder(q)
    case q: RegexQueryDefinition => RegexQueryBuilder(q)
    case q: RangeQueryDefinition => RangeQueryBuilder(q)
    case q: SimpleStringQueryDefinition => SimpleStringQueryBuilder(q)
    case q: GeoPolygonQueryDefinition => GeoPolygonQueryBuilder(q)
    case q: TermsQueryDefinition[_] => TermsQueryBuilder(q)
    case q: DisMaxDefinition => DisMaxBuilder(q)
    case q: ScriptQueryDefinition => ScriptQueryBuilder(q)
    case q: BoolQueryDefinition => BoolQueryBuilder(q)
    case q: MatchPhrasePrefixDefinition => MatchPhrasePrefixBuilder(q)
    case q: TypeQueryDefinition => QueryBuilders.typeQuery(q.`type`)
    case q: MultiMatchQueryDefinition => MultiMatchQueryBuilderFn(q)
    case q: ConstantScoreDefinition => ConstantScoreBuilder(q)
  }
}
