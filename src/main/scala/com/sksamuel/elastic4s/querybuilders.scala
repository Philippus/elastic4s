package com.sksamuel.elastic4s

import org.elasticsearch.index.query.{QueryStringQueryBuilder, RegexpFlag, QueryBuilders}

/** @author Stephen Samuel */
trait QueryBuilder[T] {
    val builder: org.elasticsearch.index.query.QueryBuilder
}

abstract class BoostableQueryBuilder[T] extends QueryBuilder[T] {
    val builder: org.elasticsearch.index.query.BoostableQueryBuilder
    def boost(boost: Double): T = {
        builder.boost(boost.toFloat)
        this.asInstanceOf[T]
    }
}

class PrefixQueryBuilder(field: String, prefix: Any) extends BoostableQueryBuilder[PrefixQueryBuilder] with QueryBuilder {
    val builder = QueryBuilders.prefixQuery(field, prefix.toString)
    def rewrite(rewrite: String) = {
        builder.rewrite(rewrite)
        this
    }
}
class RegexQueryBuilder(field: String, regex: Any) extends BoostableQueryBuilder[RegexQueryBuilder] with QueryBuilder {
    val builder = QueryBuilders.regexpQuery(field, regex.toString)
    def flags(flags: RegexpFlag*) {
        builder.flags(flags: _*)
    }
    def rewrite(rewrite: String) = {
        builder.rewrite(rewrite)
        this
    }
}
class TermQueryBuilder(field: String, value: Any) extends BoostableQueryBuilder[TermQueryBuilder] with QueryBuilder {
    val builder = QueryBuilders.termQuery(field, value.toString)
}
class MatchAllQueryBuilder extends BoostableQueryBuilder[MatchAllQueryBuilder] with QueryBuilder {

    val builder = QueryBuilders.matchAllQuery

    def normsField(normsField: String) = {
        builder.normsField(normsField)
    }
}

class RangeQueryBuilder(field: String) extends BoostableQueryBuilder[RangeQueryBuilder] with QueryBuilder {

    val builder = QueryBuilders.rangeQuery(field)

    def from(from: Any) = {
        builder.from(from)
        this
    }

    def to(to: Any) = {
        builder.to(to)
        this
    }

    def includeLower(includeLower: Boolean) = {
        builder.includeLower(includeLower)
        this
    }

    def includeUpper(includeUpper: Boolean) = {
        builder.includeUpper(includeUpper)
        this
    }
}

class MatchQueryBuilder(field: String, value: Any) extends BoostableQueryBuilder[MatchQueryBuilder] with QueryBuilder {

    val builder = QueryBuilders.matchQuery(field, value)

    def operator(op: String) = {
        op match {
            case "AND" => builder.operator(org.elasticsearch.index.query.MatchQueryBuilder.Operator.AND)
            case _ => builder.operator(org.elasticsearch.index.query.MatchQueryBuilder.Operator.OR)
        }
        this
    }

    def analyzer(a: Analyzer) = {
        builder.analyzer(a.elastic)
        this
    }
}

class StringQueryBuilder(query: String) extends BoostableQueryBuilder[StringQueryBuilder] {

    val builder = QueryBuilders.queryString(query)

    def operator(op: String) = {
        op.toLowerCase match {
            case "AND" => builder.defaultOperator(QueryStringQueryBuilder.Operator.AND)
            case _ => builder.defaultOperator(QueryStringQueryBuilder.Operator.OR)
        }
        this
    }

    def fuzzyMaxExpansions(fuzzyMaxExpansions: Int) = {
        builder.fuzzyMaxExpansions(fuzzyMaxExpansions)
        this
    }

    def lenient(lenient: Boolean) = {
        builder.lenient(lenient)
        this
    }

    def phraseSlop(phraseSlop: Int) = {
        builder.phraseSlop(phraseSlop)
        this
    }

    def tieBreaker(tieBreaker: Double) = {
        builder.tieBreaker(tieBreaker.toFloat)
        this
    }

    def fuzzyPrefixLength(fuzzyPrefixLength: Int) = {
        builder.fuzzyPrefixLength(fuzzyPrefixLength)
        this
    }

    def fuzzyMinSim(fuzzyMinSim: Double) = {
        builder.fuzzyMinSim(fuzzyMinSim.toFloat)
        this
    }

    def anaylyzer(analyzer: Analyzer) = {
        builder.analyzer(analyzer.elastic)
        this
    }

    def defaultField(field: String) = {
        builder.defaultField(field)
        this
    }

    def analyzeWildcard(analyzeWildcard: Boolean) = {
        builder.analyzeWildcard(analyzeWildcard)
        this
    }

    def rewrite(value: String) = {
        builder.rewrite(value)
        this
    }

    def autoGeneratePhraseQueries(autoGeneratePhraseQueries: Boolean) = {
        builder.autoGeneratePhraseQueries(autoGeneratePhraseQueries)
        this
    }

    def allowLeadingWildcard(allowLeadingWildcard: Boolean) = {
        builder.allowLeadingWildcard(allowLeadingWildcard)
        this
    }

    def enablePositionIncrements(enablePositionIncrements: Boolean) = {
        builder.enablePositionIncrements(enablePositionIncrements)
        this
    }
}
