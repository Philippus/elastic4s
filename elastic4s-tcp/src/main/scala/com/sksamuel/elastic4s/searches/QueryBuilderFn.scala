package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.searches.queries._
import org.elasticsearch.index.query.{QueryBuilder, QueryBuilders, RangeQueryBuilder, RegexpQueryBuilder}

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
  }
}

object RangeQueryBuilder {
  def apply(q: RangeQueryDefinition): RangeQueryBuilder = {
    val builder = QueryBuilders.rangeQuery(q.field)
    q.queryName.foreach(builder.queryName)
    q.boost.map(_.toFloat).foreach(builder.boost)
    q.from.foreach(builder.from)
    q.to.foreach(builder.to)
    q.gte.foreach(builder.gte)
    q.lte.foreach(builder.lte)
    q.includeLower.foreach(builder.includeLower)
    q.includeUpper.foreach(builder.includeUpper)
    q.timeZone.foreach(builder.timeZone)
    builder
  }
}

object RegexQueryBuilder {
  def apply(q: RegexQueryDefinition): RegexpQueryBuilder = {
    val builder = QueryBuilders.regexpQuery(q.field, q.regex)
    if (q.flags.nonEmpty)
      builder.flags(q.flags.map(org.elasticsearch.index.query.RegexpFlag.valueOf): _*)
    q.queryName.foreach(builder.queryName)
    q.boost.map(_.toFloat).foreach(builder.boost)
    q.rewrite.foreach(builder.rewrite)
    builder
  }
}












