package com.sksamuel.elastic4s

import org.elasticsearch.index.query.{QueryStringQueryBuilder, RegexpFlag, QueryBuilders}

/** @author Stephen Samuel */

trait QueryDsl {

    def regex(tuple: (String, Any)): RegexQueryDefinition = regex(tuple._1, tuple._2)
    def regex(field: String, value: Any): RegexQueryDefinition = new RegexQueryDefinition(field, value)

    def term(tuple: (String, Any)): TermQueryDefinition = term(tuple._1, tuple._2)
    def term(field: String, value: Any): TermQueryDefinition = new TermQueryDefinition(field, value)

    def prefix(tuple: (String, Any)): PrefixQueryDefinition = prefix(tuple._1, tuple._2)
    def prefix(field: String, value: Any): PrefixQueryDefinition = new PrefixQueryDefinition(field, value)

    def matches(tuple: (String, Any)): MatchQueryDefinition = matches(tuple._1, tuple._2)
    def matches(field: String, value: Any): MatchQueryDefinition = new MatchQueryDefinition(field, value)

    def query(tuple: (String, Any)): StringQueryDefinition = query(tuple._1, tuple._2)
    def query(field: String, value: Any): StringQueryDefinition = new StringQueryDefinition(field)

    def all: MatchAllQueryDefinition = new MatchAllQueryDefinition
}

trait QueryDefinition {
    val builder: org.elasticsearch.index.query.QueryBuilder
}

class PrefixQueryDefinition(field: String, prefix: Any) extends QueryDefinition {
    val builder = QueryBuilders.prefixQuery(field, prefix.toString)
    def rewrite(rewrite: String) = {
        builder.rewrite(rewrite)
        this
    }
    def boost(boost: Double) = {
        builder.boost(boost.toFloat)
        this
    }
}
class RegexQueryDefinition(field: String, regex: Any) extends QueryDefinition {

    val builder = QueryBuilders.regexpQuery(field, regex.toString)
    def flags(flags: RegexpFlag*) {
        builder.flags(flags: _*)
    }
    def rewrite(rewrite: String) = {
        builder.rewrite(rewrite)
        this
    }
    def boost(boost: Double) = {
        builder.boost(boost.toFloat)
        this
    }

    def and(builder: QueryDefinition) = builder
}
class TermQueryDefinition(field: String, value: Any) extends QueryDefinition {
    val builder = QueryBuilders.termQuery(field, value.toString)
    def boost(boost: Double) = {
        builder.boost(boost.toFloat)
        this
    }
}
class MatchAllQueryDefinition extends QueryDefinition {

    val builder = QueryBuilders.matchAllQuery

    def normsField(normsField: String) = {
        builder.normsField(normsField)
    }
    def boost(boost: Double) = {
        builder.boost(boost.toFloat)
        this
    }
}

class RangeQueryBuilder(field: String) extends QueryDefinition {

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

    def boost(boost: Double) = {
        builder.boost(boost.toFloat)
        this
    }
}

class MatchQueryDefinition(field: String, value: Any) extends QueryDefinition {

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

    def boost(boost: Double) = {
        builder.boost(boost.toFloat)
        this
    }
}

class StringQueryDefinition(query: String) extends QueryDefinition {

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

    def boost(boost: Double) = {
        builder.boost(boost.toFloat)
        this
    }
}
class BoolQueryBuilder extends QueryDefinition {

    val builder = QueryBuilders.boolQuery()

    def minimumNumberShouldMatch(minimumNumberShouldMatch: Int) = {
        builder.minimumNumberShouldMatch(minimumNumberShouldMatch)
        this
    }

    def must(builder: QueryDefinition) {

    }
}