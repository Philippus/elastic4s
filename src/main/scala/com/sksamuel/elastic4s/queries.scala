package com.sksamuel.elastic4s

import org.elasticsearch.index.query.{QueryStringQueryBuilder, RegexpFlag, QueryBuilders}

/** @author Stephen Samuel */

trait QueryDsl {

    def query = this

    def regex(tuple: (String, Any)): RegexQueryDefinition = regex(tuple._1, tuple._2)
    def regex(field: String, value: Any): RegexQueryDefinition = new RegexQueryDefinition(field, value)

    def range(field: String): RangeQueryDefinition = new RangeQueryDefinition(field)

    def term(tuple: (String, Any)): TermQueryDefinition = term(tuple._1, tuple._2)
    def term(field: String, value: Any): TermQueryDefinition = new TermQueryDefinition(field, value)

    def prefix(tuple: (String, Any)): PrefixQueryDefinition = prefix(tuple._1, tuple._2)
    def prefix(field: String, value: Any): PrefixQueryDefinition = new PrefixQueryDefinition(field, value)

    def matches(tuple: (String, Any)): MatchQueryDefinition = matches(tuple._1, tuple._2)
    def matches(field: String, value: Any): MatchQueryDefinition = new MatchQueryDefinition(field, value)

    def query(value: String): StringQueryDefinition = new StringQueryDefinition(value)

    def field(tuple: (String, Any)): FieldQueryDefinition = field(tuple._1, tuple._2)
    def field(field: String, value: Any): FieldQueryDefinition = new FieldQueryDefinition(field, value)

    def wildcard(tuple: (String, Any)): WildcardQueryDefinition = wildcard(tuple._1, tuple._2)
    def wildcard(field: String, value: Any): WildcardQueryDefinition = new WildcardQueryDefinition(field, value)

    def ids(ids: String*) = new IdQueryDefinition(ids: _*)
    def all: MatchAllQueryDefinition = new MatchAllQueryDefinition

    def bool(block: => BoolQueryDefinition): QueryDefinition = block

    def must(queries: QueryDefinition*): BoolQueryDefinition = new BoolQueryDefinition().must(queries: _*)
    def should(queries: QueryDefinition*): BoolQueryDefinition = new BoolQueryDefinition().should(queries: _*)
    def not(queries: QueryDefinition*): BoolQueryDefinition = new BoolQueryDefinition().not(queries: _*)
}

class BoolQueryDefinition extends QueryDefinition {
    val builder = QueryBuilders.boolQuery()
    def must(queries: QueryDefinition*) = this
    def should(queries: QueryDefinition*) = this
    def not(queries: QueryDefinition*) = this
}

trait QueryDefinition {
    val builder: org.elasticsearch.index.query.QueryBuilder
}

class FilteredQueryDefinition extends QueryDefinition {
    val builder = QueryBuilders.filteredQuery(null, null)
    def boost(boost: Double) = {
        builder.boost(boost.toFloat)
        this
    }
}

class IdQueryDefinition(ids: String*) extends FilterDefinition {
    var builder = QueryBuilders.idsQuery().addIds(ids: _*)
    var boost: Double = -1
    def types(types: String*) = {
        builder = QueryBuilders.idsQuery(types: _*).addIds(ids: _*).boost(boost.toFloat)
        this
    }
    def boost(boost: Double) = {
        builder.boost(boost.toFloat)
        this.boost = boost
        this
    }
}

class BoostingQueryDefinition extends QueryDefinition {
    val builder = QueryBuilders.boostingQuery()
}

class WildcardQueryDefinition(field: String, query: Any) extends QueryDefinition {
    val builder = QueryBuilders.wildcardQuery(field, query.toString)
    def rewrite(rewrite: String) = {
        builder.rewrite(rewrite)
        this
    }
    def boost(boost: Double) = {
        builder.boost(boost.toFloat)
        this
    }
}

class FieldQueryDefinition(field: String, query: Any) extends QueryDefinition {
    val builder = QueryBuilders.fieldQuery(field, query)
    def rewrite(rewrite: String) = {
        builder.rewrite(rewrite)
        this
    }
    def boost(boost: Double) = {
        builder.boost(boost.toFloat)
        this
    }
    def fuzzyMaxExpansions(fuzzyMaxExpansions: Int) = {
        builder.fuzzyMaxExpansions(fuzzyMaxExpansions)
        this
    }

    def phraseSlop(phraseSlop: Int) = {
        builder.phraseSlop(phraseSlop)
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

    def analyzeWildcard(analyzeWildcard: Boolean) = {
        builder.analyzeWildcard(analyzeWildcard)
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

class RangeQueryDefinition(field: String) extends QueryDefinition {

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

