package com.sksamuel.elastic4s

import org.elasticsearch.index.query._
import scala.Predef.String

/** @author Stephen Samuel */

trait QueryDsl {

    implicit def string2query(string: String) = new StringQueryDefinition(string)

    def query = this

    def regex(tuple: (String, Any)): RegexQueryDefinition = regex(tuple._1, tuple._2)
    def regex(field: String, value: Any): RegexQueryDefinition = regexQuery(field, value)
    def regexQuery(tuple: (String, Any)): RegexQueryDefinition = regexQuery(tuple._1, tuple._2)
    def regexQuery(field: String, value: Any): RegexQueryDefinition = new RegexQueryDefinition(field, value)

    def boosting = new BoostingQueryDefinition
    def boostinguery = new BoostingQueryDefinition

    def range(field: String): RangeQueryDefinition = rangeQuery(field)
    def rangeQuery(field: String): RangeQueryDefinition = new RangeQueryDefinition(field)

    def term(tuple: (String, Any)): TermQueryDefinition = termQuery(tuple._1, tuple._2)
    def term(field: String, value: Any): TermQueryDefinition = termQuery(field, value)
    def termQuery(tuple: (String, Any)): TermQueryDefinition = term(tuple._1, tuple._2)
    def termQuery(field: String, value: Any): TermQueryDefinition = new TermQueryDefinition(field, value)

    def prefix(tuple: (String, Any)): PrefixQueryDefinition = prefix(tuple._1, tuple._2)
    def prefix(field: String, value: Any): PrefixQueryDefinition = prefixQuery(field, value)
    def prefixQuery(tuple: (String, Any)): PrefixQueryDefinition = prefixQuery(tuple._1, tuple._2)
    def prefixQuery(field: String, value: Any): PrefixQueryDefinition = new PrefixQueryDefinition(field, value)

    def multiMatchQuery(text: String) = new MultiMatchQueryDefinition(text)

    def filter = filterQuery
    def fuzzy(name: String, value: Any) = fuzzyQuery(name, value)
    def filterQuery = new FilteredQueryDefinition
    def fuzzyQuery(name: String, value: Any) = new FuzzyDefinition(name, value)

    def matches(tuple: (String, Any)): MatchQueryDefinition = matchQuery(tuple._1, tuple._2)
    def matches(field: String, value: Any): MatchQueryDefinition = matchQuery(field, value)
    def matchQuery(tuple: (String, Any)): MatchQueryDefinition = matchQuery(tuple._1, tuple._2)
    def matchQuery(field: String, value: Any): MatchQueryDefinition = new MatchQueryDefinition(field, value)

    def matchall = new MatchAllQueryDefinition

    def query(value: String): StringQueryDefinition = new StringQueryDefinition(value)

    def field(tuple: (String, Any)): FieldQueryDefinition = fieldQuery(tuple._1, tuple._2)
    def field(field: String, value: Any): FieldQueryDefinition = fieldQuery(field, value)
    def fieldQuery(tuple: (String, Any)): FieldQueryDefinition = fieldQuery(tuple._1, tuple._2)
    def fieldQuery(field: String, value: Any): FieldQueryDefinition = new FieldQueryDefinition(field, value)

    def wildcard(tuple: (String, Any)): WildcardQueryDefinition = wildcardQuery(tuple._1, tuple._2)
    def wildcard(field: String, value: Any): WildcardQueryDefinition = wildcardQuery(field, value)
    def wildcardQuery(tuple: (String, Any)): WildcardQueryDefinition = wildcardQuery(tuple._1, tuple._2)
    def wildcardQuery(field: String, value: Any): WildcardQueryDefinition = new WildcardQueryDefinition(field, value)

    def ids(iterable: Iterable[String]): IdQueryDefinition = ids(iterable.toSeq: _*)
    def ids(ids: String*): IdQueryDefinition = new IdQueryDefinition(ids: _*)
    def all: MatchAllQueryDefinition = new MatchAllQueryDefinition

    def bool(block: => BoolQueryDefinition): QueryDefinition = block

    def must(queries: QueryDefinition*): BoolQueryDefinition = new BoolQueryDefinition().must(queries: _*)
    def should(queries: QueryDefinition*): BoolQueryDefinition = new BoolQueryDefinition().should(queries: _*)
    def not(queries: QueryDefinition*): BoolQueryDefinition = new BoolQueryDefinition().not(queries: _*)
}

class BoolQueryDefinition extends QueryDefinition {
    val builder = QueryBuilders.boolQuery()
    def must(queries: QueryDefinition*) = {
        queries.foreach(builder must _.builder)
        this
    }
    def should(queries: QueryDefinition*) = {
        queries.foreach(builder should _.builder)
        this
    }
    def not(queries: QueryDefinition*) = {
        queries.foreach(builder mustNot _.builder)
        this
    }
}

trait QueryDefinition {
    def builder: org.elasticsearch.index.query.QueryBuilder
}

class MultiMatchQueryDefinition(text: String) extends QueryDefinition {
    val builder = QueryBuilders.multiMatchQuery(text)
    def maxExpansions(maxExpansions: Int): MultiMatchQueryDefinition = {
        builder.maxExpansions(maxExpansions)
        this
    }
    def fields(_fields: Iterable[String]) = {
        for ( f <- _fields ) builder.field(f)
        this
    }
    def fields(_fields: String*): MultiMatchQueryDefinition = fields(_fields.toIterable)
    def boost(boost: Double): MultiMatchQueryDefinition = {
        builder.boost(boost.toFloat)
        this
    }
    def analyzer(a: Analyzer): MultiMatchQueryDefinition = analyzer(a.elastic)
    def analyzer(a: String): MultiMatchQueryDefinition = {
        builder.analyzer(a)
        this
    }
    def prefixLength(prefixLength: Int): MultiMatchQueryDefinition = {
        builder.prefixLength(prefixLength)
        this
    }
    def fuzziness(f: AnyRef): MultiMatchQueryDefinition = {
        builder.fuzziness(f)
        this
    }
    def fuzzyRewrite(fuzzyRewrite: String): MultiMatchQueryDefinition = {
        builder.fuzzyRewrite(fuzzyRewrite)
        this
    }
    def minimumShouldMatch(minimumShouldMatch: Int): MultiMatchQueryDefinition = {
        builder.minimumShouldMatch(minimumShouldMatch.toString)
        this
    }
    def useDisMax(useDisMax: Boolean): MultiMatchQueryDefinition = {
        builder.useDisMax(java.lang.Boolean.valueOf(useDisMax))
        this
    }
    def lenient(l: Boolean): MultiMatchQueryDefinition = {
        builder.lenient(l)
        this
    }
    def cutoffFrequency(cutoffFrequency: Double): MultiMatchQueryDefinition = {
        builder.cutoffFrequency(cutoffFrequency.toFloat)
        this
    }
    def zeroTermsQuery(q: MatchQueryBuilder.ZeroTermsQuery): MultiMatchQueryDefinition = {
        builder.zeroTermsQuery(q)
        this
    }
    def tieBreaker(tieBreaker: Double): MultiMatchQueryDefinition = {
        builder.tieBreaker(java.lang.Float.valueOf(tieBreaker.toFloat))
        this
    }
}

class FuzzyDefinition(name: String, value: Any) extends QueryDefinition {
    val builder = QueryBuilders.fuzzyQuery(name, value.toString)
    def minSimilarity(minSimilarity: Double) = {
        builder.minSimilarity(minSimilarity.toString)
        this
    }
    def maxExpansions(maxExpansions: Int) = {
        builder.maxExpansions(maxExpansions)
        this
    }
    def boost(boost: Double) = {
        builder.boost(boost.toFloat)
        this
    }
    def transpositions(transpositions: Boolean) = {
        builder.transpositions(transpositions)
        this
    }
    def prefixLength(prefixLength: Int) = {
        builder.prefixLength(prefixLength)
        this
    }
}

class FilteredQueryDefinition extends QueryDefinition {
    def builder = QueryBuilders.filteredQuery(_query, _filter).boost(_boost.toFloat)
    var _query: QueryBuilder = null
    var _filter: FilterBuilder = null
    var _boost: Double = 0d
    def boost(boost: Double): FilteredQueryDefinition = {
        _boost = boost
        this
    }
    def query(query: => QueryDefinition): FilteredQueryDefinition = {
        _query = query.builder
        this
    }
    def filter(filter: => FilterDefinition): FilteredQueryDefinition = {
        _filter = filter.builder
        this
    }
}

class IdQueryDefinition(ids: String*) extends QueryDefinition {

    def builder = _builder
    var _builder = QueryBuilders.idsQuery().addIds(ids: _*)
    var _boost: Double = -1

    def types(types: String*) = {
        _builder = QueryBuilders.idsQuery(types: _*).addIds(ids: _*).boost(_boost.toFloat)
        this
    }
    def boost(boost: Double) = {
        _builder.boost(boost.toFloat)
        _boost = boost
        this
    }
}

class BoostingQueryDefinition extends QueryDefinition {
    val builder = QueryBuilders.boostingQuery()
    def positive(block: => QueryDefinition) = {
        builder.positive(block.builder)
        this
    }
    def negative(block: => QueryDefinition) = {
        builder.negative(block.builder)
        this
    }
    def positiveBoost(b: Double) = {
        builder.boost(b.toFloat)
        this
    }
    def negativeBoost(b: Double) = {
        builder.negativeBoost(b.toFloat)
        this
    }
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

    def analyzer(analyzer: Analyzer) = {
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
    def flags(flags: RegexpFlag*): RegexQueryDefinition = {
        builder.flags(flags: _*)
        this
    }
    def rewrite(rewrite: String): RegexQueryDefinition = {
        builder.rewrite(rewrite)
        this
    }
    def boost(boost: Double): RegexQueryDefinition = {
        builder.boost(boost.toFloat)
        this
    }
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

    def normsField(normsField: String): MatchAllQueryDefinition = {
        builder.normsField(normsField)
        this
    }
    def boost(boost: Double): MatchAllQueryDefinition = {
        builder.boost(boost.toFloat)
        this
    }
}

class RangeQueryDefinition(field: String) extends QueryDefinition {

    val builder = QueryBuilders.rangeQuery(field)

    def from(f: Any) = {
        builder.from(f)
        this
    }

    def to(t: Any) = {
        builder.to(t)
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

    def lenient(l: Boolean) = {
        builder.lenient(java.lang.Boolean.valueOf(l))
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

