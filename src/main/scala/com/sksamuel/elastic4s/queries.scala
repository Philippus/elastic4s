package com.sksamuel.elastic4s

import org.elasticsearch.index.query._
import org.elasticsearch.index.query.CommonTermsQueryBuilder.Operator
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder
import com.sksamuel.elastic4s.DefinitionAttributes._
import scala.Some

/** @author Stephen Samuel */

trait QueryDsl {

  implicit def string2query(string: String) = new SimpleStringQueryDefinition(string)
  implicit def tuple2query(kv: (String, String)) = new TermQueryDefinition(kv._1, kv._2)

  def query = this

  def boosting: BoostingQueryDefinition = boostingQuery
  def boostingQuery: BoostingQueryDefinition = new BoostingQueryDefinition

  def commonQuery(field: String) = new CommonQueryExpectsText(field)
  def commonQuery = new CommonQueryExpectsField
  class CommonQueryExpectsField {
    def field(name: String) = new CommonQueryExpectsText(name)
  }
  class CommonQueryExpectsText(name: String) {
    def text(q: String): CommonQueryDefinition = new CommonQueryDefinition(name, q)
    def query(q: String): CommonQueryDefinition = text(q)
  }

  @deprecated("@deprecated use functionScoreQuery instead", "0.90.8")
  def customScore = new CustomScoreDefinition
  @deprecated("@deprecated use functionScoreQuery instead", "0.90.8")
  def customBoost = new CustomBoostExpectingQuery
  @deprecated("@deprecated use functionScoreQuery instead", "0.90.8")
  class CustomBoostExpectingQuery {
    def query(query: QueryDefinition) = new CustomBoostFactorQueryDefinition(query)
  }
  def constantScore = new ConstantScoreExpectsQueryOrFilter
  class ConstantScoreExpectsQueryOrFilter {
    def query(query: QueryDefinition) = new ConstantScoreDefinition(QueryBuilders.constantScoreQuery(query.builder))
    def filter(filter: FilterDefinition) = new ConstantScoreDefinition(QueryBuilders.constantScoreQuery(filter.builder))
  }

  def dismax = new DisMaxDefinition

  def field(tuple: (String, Any)): FieldQueryDefinition = fieldQuery(tuple)
  def field(field: String, value: Any): FieldQueryDefinition = fieldQuery(field, value)
  def fieldQuery(tuple: (String, Any)): FieldQueryDefinition = fieldQuery(tuple._1, tuple._2)
  def fieldQuery(field: String, value: Any): FieldQueryDefinition = new FieldQueryDefinition(field, value)

  def fuzzylikethis: FuzzyLikeThisDefinitionExpectsText = flt
  def flt: FuzzyLikeThisDefinitionExpectsText = new FuzzyLikeThisDefinitionExpectsText
  def flt(text: String): FuzzyLikeThisExpectsField = new FuzzyLikeThisExpectsField(text)
  def fuzzylikethis(text: String): FuzzyLikeThisExpectsField = flt(text)
  class FuzzyLikeThisDefinitionExpectsText {
    def text(q: String) = new FuzzyLikeThisExpectsField(q)
  }
  class FuzzyLikeThisExpectsField(text: String) {
    def field(name: String): FuzzyLikeThisDefinition = fields(name)
    def fields(names: String*): FuzzyLikeThisDefinition = new FuzzyLikeThisDefinition(text, names)
  }

  def functionScoreQuery(query: QueryDefinition): FunctionScoreQueryDefinition =
    new FunctionScoreQueryDefinition(Left(query))
  def functionScoreQuery(filter: FilterDefinition): FunctionScoreQueryDefinition =
    new FunctionScoreQueryDefinition(Right(filter))

  @deprecated("ambigious, use filteredQuery", "0.90.3.3")
  def filter = filterQuery
  def fuzzy(name: String, value: Any) = fuzzyQuery(name, value)
  @deprecated("use filteredQuery", "0.90.3.3")
  def filterQuery = filteredQuery
  def filteredQuery = new FilteredQueryDefinition
  def fuzzyQuery(name: String, value: Any) = new FuzzyDefinition(name, value)

  def hasChildQuery = new HasChildExpectsType
  def hasChildQuery(`type`: String) = new HasChildExpectsQuery(`type`)
  class HasChildExpectsType {
    def typed(`type`: String): HasChildExpectsQuery = new HasChildExpectsQuery(`type`)
  }
  class HasChildExpectsQuery(`type`: String) {
    def query(q: QueryDefinition): HasChildQueryDefinition = new HasChildQueryDefinition(`type`, q)
  }

  def hasParentQuery = new HasParentExpectsType
  def hasParentQuery(`type`: String) = new HasParentExpectsQuery(`type`)
  class HasParentExpectsType {
    def typed(`type`: String) = new HasParentExpectsQuery(`type`)
  }
  class HasParentExpectsQuery(`type`: String) {
    def query(q: QueryDefinition) = new HasParentQueryDefinition(`type`, q)
  }

  def matches(tuple: (String, Any)): MatchQueryDefinition = matchQuery(tuple)
  def matches(field: String, value: Any): MatchQueryDefinition = matchQuery(field, value)
  def matchQuery(tuple: (String, Any)): MatchQueryDefinition = matchQuery(tuple._1, tuple._2)
  def matchQuery(field: String, value: Any): MatchQueryDefinition = new MatchQueryDefinition(field, value)
  def matchPhrase(field: String, value: Any): MatchPhraseDefinition = new MatchPhraseDefinition(field, value)
  def matchPhrasePrefix(field: String, value: Any): MatchPhrasePrefixDefinition =
    new MatchPhrasePrefixDefinition(field, value)

  def multiMatchQuery(text: String) = new MultiMatchQueryDefinition(text)
  def matchall = new MatchAllQueryDefinition

  def nested(path: String): NestedQueryDefinition = new NestedQueryDefinition(path)

  def query(q: String): StringQueryDefinition = new StringQueryDefinition(q)

  def range(field: String): RangeQueryDefinition = rangeQuery(field)
  def rangeQuery(field: String): RangeQueryDefinition = new RangeQueryDefinition(field)

  def regex(tuple: (String, Any)): RegexQueryDefinition = regex(tuple._1, tuple._2)
  def regex(field: String, value: Any): RegexQueryDefinition = regexQuery(field, value)
  def regexQuery(tuple: (String, Any)): RegexQueryDefinition = regexQuery(tuple._1, tuple._2)
  def regexQuery(field: String, value: Any): RegexQueryDefinition = new RegexQueryDefinition(field, value)

  def prefix(tuple: (String, Any)): PrefixQueryDefinition = prefixQuery(tuple)
  def prefix(field: String, value: Any): PrefixQueryDefinition = prefixQuery(field, value)
  def prefixQuery(tuple: (String, Any)): PrefixQueryDefinition = prefixQuery(tuple._1, tuple._2)
  def prefixQuery(field: String, value: Any): PrefixQueryDefinition = new PrefixQueryDefinition(field, value)

  def simpleStringQuery(q: String): SimpleStringQueryDefinition = new SimpleStringQueryDefinition(q)
  def stringQuery(q: String) = new StringQueryDefinition(q)

  def spanOrQuery = new SpanOrQueryDefinition
  def spanTermQuery(field: String, value: Any): SpanTermQueryDefinition = new SpanTermQueryDefinition(field, value)

  def term(tuple: (String, Any)): TermQueryDefinition = termQuery(tuple)
  def term(field: String, value: Any): TermQueryDefinition = termQuery(field, value)
  def termQuery(tuple: (String, Any)): TermQueryDefinition = termQuery(tuple._1, tuple._2)
  def termQuery(field: String, value: Any): TermQueryDefinition = new TermQueryDefinition(field, value)

  def termsQuery(field: String, values: AnyRef*): TermsQueryDefinition =
    new TermsQueryDefinition(field, values.map(_.toString): _*)

  def topChildren(`type`: String) = new TopChildrenExpectsQuery(`type`)
  class TopChildrenExpectsQuery(`type`: String) {
    def query(q: QueryDefinition) = new TopChildrenQueryDefinition(`type`, q)
  }

  def wildcard(tuple: (String, Any)): WildcardQueryDefinition = wildcardQuery(tuple)
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

class FunctionScoreQueryDefinition(queryOrFilter: Either[QueryDefinition, FilterDefinition]) extends QueryDefinition {

  val builder = if (queryOrFilter.isLeft)
    new FunctionScoreQueryBuilder(queryOrFilter.left.get.builder)
  else
    new FunctionScoreQueryBuilder(queryOrFilter.right.get.builder)

  def boost(boost: Double): FunctionScoreQueryDefinition = {
    builder.boost(boost.toFloat)
    this
  }
  def boostMode(boostMode: String): FunctionScoreQueryDefinition = {
    builder.boostMode(boostMode)
    this
  }
  def maxBoost(maxBoost: Double): FunctionScoreQueryDefinition = {
    builder.maxBoost(maxBoost.toFloat)
    this
  }
  def scoreMode(scoreMode: String): FunctionScoreQueryDefinition = {
    builder.scoreMode(scoreMode)
    this
  }
  def scorers(scorers: ScoreDefinition[_]*): FunctionScoreQueryDefinition = {
    scorers.foreach(scorer => scorer._filter match {
      case None => builder.add(scorer.builder)
      case Some(filter) => builder.add(filter.builder, scorer.builder)
    })
    this
  }
}

class MultiMatchQueryDefinition(text: String)
  extends QueryDefinition
  with DefinitionAttributeFuzziness
  with DefinitionAttributePrefixLength
  with DefinitionAttributeFuzzyRewrite {

  val _builder = QueryBuilders.multiMatchQuery(text)
  val builder = _builder

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
  def analyzer(a: Analyzer): MultiMatchQueryDefinition = analyzer(a.name)
  def analyzer(a: String): MultiMatchQueryDefinition = {
    builder.analyzer(a)
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

class FuzzyDefinition(name: String, value: Any) extends QueryDefinition with DefinitionAttributePrefixLength {
  val builder = QueryBuilders.fuzzyQuery(name, value.toString)
  val _builder = builder
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
}

class HasChildQueryDefinition(`type`: String, q: QueryDefinition) extends QueryDefinition {
  val builder = QueryBuilders.hasChildQuery(`type`, q.builder)
  def scoreType(scoreType: String): HasChildQueryDefinition = {
    builder.scoreType(scoreType)
    this
  }
  def boost(boost: Double) = {
    builder.boost(boost.toFloat)
    this
  }
}

class HasParentQueryDefinition(`type`: String, q: QueryDefinition) extends QueryDefinition {
  val builder = QueryBuilders.hasParentQuery(`type`, q.builder)
  def boost(boost: Double) = {
    builder.boost(boost.toFloat)
    this
  }
  def scoreType(scoreType: String): HasParentQueryDefinition = {
    builder.scoreType(scoreType)
    this
  }
}

@deprecated("@deprecated use functionScoreQuery instead", "0.90.8")
class CustomScoreDefinition extends QueryDefinition {
  private var _query: QueryDefinition = _
  private var _boost: Double = _
  private var _lang: String = _
  private var _script: String = _
  def builder = {
    require(_query != null, "must specify query for custom score query")
    QueryBuilders.customScoreQuery(_query.builder).script(_script).lang(_lang).boost(_boost.toFloat)
  }
  def query(query: QueryDefinition): CustomScoreDefinition = {
    this._query = query
    this
  }
  def boost(b: Double): CustomScoreDefinition = {
    _boost = b
    this
  }
  def script(script: String): CustomScoreDefinition = {
    _script = script
    this
  }
  def lang(lang: String): CustomScoreDefinition = {
    _lang = lang
    this
  }
}

class ConstantScoreDefinition(val builder: ConstantScoreQueryBuilder) extends QueryDefinition {
  def boost(b: Double): QueryDefinition = {
    builder.boost(b.toFloat)
    this
  }
}

class FuzzyLikeThisDefinition(text: String, fields: Iterable[String])
  extends QueryDefinition
  with DefinitionAttributePrefixLength
  with DefinitionAttributeBoost {

  val builder = fields.size match {
    case 0 => QueryBuilders.fuzzyLikeThisQuery().likeText(text)
    case _ => QueryBuilders.fuzzyLikeThisQuery(fields.toSeq: _*).likeText(text)
  }
  val _builder = builder

  def analyzer(a: Analyzer): FuzzyLikeThisDefinition = {
    builder.analyzer(a.name)
    this
  }
  def ignoreTF(b: Boolean): FuzzyLikeThisDefinition = {
    builder.ignoreTF(b)
    this
  }
  def maxQueryTerms(b: Int): FuzzyLikeThisDefinition = {
    builder.maxQueryTerms(b)
    this
  }
  def minSimilarity(b: Double): FuzzyLikeThisDefinition = {
    builder.minSimilarity(b.toFloat)
    this
  }
}

class CommonQueryDefinition(name: String, text: String) extends QueryDefinition {
  val builder = QueryBuilders.commonTerms(name, text)
  def boost(b: Double): CommonQueryDefinition = {
    builder.boost(b.toFloat)
    this
  }
  def highFreqMinimumShouldMatch(highFreqMinimumShouldMatch: Double): CommonQueryDefinition = {
    builder.highFreqMinimumShouldMatch(highFreqMinimumShouldMatch.toString)
    this
  }
  def highFreqOperator(operator: String): CommonQueryDefinition = {
    builder.highFreqOperator(if (operator.toLowerCase == "and") Operator.AND else Operator.OR)
    this
  }
  def analyzer(analyzer: Analyzer): CommonQueryDefinition = {
    builder.analyzer(analyzer.name)
    this
  }
  def lowFreqMinimumShouldMatch(lowFreqMinimumShouldMatch: Double): CommonQueryDefinition = {
    builder.lowFreqMinimumShouldMatch(lowFreqMinimumShouldMatch.toString)
    this
  }
  def lowFreqOperator(operator: String): CommonQueryDefinition = {
    builder.lowFreqOperator(if (operator.toLowerCase == "and") Operator.AND else Operator.OR)
    this
  }
  def cutoffFrequency(cutoffFrequency: Double): CommonQueryDefinition = {
    builder.cutoffFrequency(cutoffFrequency.toFloat)
    this
  }
}

@deprecated("@deprecated use functionScoreQuery instead", "0.90.8")
class CustomBoostFactorQueryDefinition(query: QueryDefinition) extends QueryDefinition {
  val builder = QueryBuilders.customBoostFactorQuery(query.builder)
  def boostFactor(b: Double): CustomBoostFactorQueryDefinition = {
    builder.boostFactor(b.toFloat)
    this
  }
}

class DisMaxDefinition extends QueryDefinition {
  val builder = QueryBuilders.disMaxQuery()
  def query(queries: QueryDefinition*): DisMaxDefinition = {
    queries.foreach(q => builder.add(q.builder))
    this
  }
  def boost(b: Double): DisMaxDefinition = {
    builder.boost(b.toFloat)
    this
  }
  def tieBreaker(tieBreaker: Double): DisMaxDefinition = {
    builder.tieBreaker(tieBreaker.toFloat)
    this
  }
}

class FilteredQueryDefinition extends QueryDefinition {
  def builder = QueryBuilders.filteredQuery(_query, _filter).boost(_boost.toFloat)
  private var _query: QueryBuilder = null
  private var _filter: FilterBuilder = null
  private var _boost: Double = -1d
  def boost(boost: Double): FilteredQueryDefinition = {
    _boost = boost
    this
  }
  def query(query: => QueryDefinition): FilteredQueryDefinition = {
    _query = Option(query).map(_.builder).orNull
    this
  }
  def filter(filter: => FilterDefinition): FilteredQueryDefinition = {
    _filter = Option(filter).map(_.builder).orNull
    this
  }
}

class IdQueryDefinition(ids: String*) extends QueryDefinition {

  def builder = _builder
  private var _builder = QueryBuilders.idsQuery().addIds(ids: _*)
  private var _boost: Double = -1

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

class SpanOrQueryDefinition extends QueryDefinition with DefinitionAttributeBoost {
  val builder = QueryBuilders.spanOrQuery
  val _builder = builder
  def clause(spans: SpanTermQueryDefinition*): SpanOrQueryDefinition = {
    spans.foreach {
      span => builder.clause(span.builder)
    }
    this
  }
}

class SpanTermQueryDefinition(field: String, value: Any) extends QueryDefinition {
  val builder = QueryBuilders.spanTermQuery(field, value.toString)
  def boost(boost: Double) = {
    builder.boost(boost.toFloat)
    this
  }
}

class WildcardQueryDefinition(field: String, query: Any)
  extends QueryDefinition
  with DefinitionAttributeRewrite {
  val builder = QueryBuilders.wildcardQuery(field, query.toString)
  val _builder = builder
  def boost(boost: Double) = {
    builder.boost(boost.toFloat)
    this
  }
}

class FieldQueryDefinition(field: String, query: Any)
  extends QueryDefinition
  with DefinitionAttributeRewrite {
  val builder = QueryBuilders.fieldQuery(field, query)
  val _builder = builder
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
    builder.analyzer(analyzer.name)
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

class PrefixQueryDefinition(field: String, prefix: Any) extends QueryDefinition with DefinitionAttributeRewrite {
  val builder = QueryBuilders.prefixQuery(field, prefix.toString)
  val _builder = builder
  def boost(boost: Double) = {
    builder.boost(boost.toFloat)
    this
  }
}
class RegexQueryDefinition(field: String, regex: Any) extends QueryDefinition with DefinitionAttributeRewrite {
  val builder = QueryBuilders.regexpQuery(field, regex.toString)
  val _builder = builder
  def flags(flags: RegexpFlag*): RegexQueryDefinition = {
    builder.flags(flags: _*)
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

class TermsQueryDefinition(field: String, values: String*) extends QueryDefinition {
  val builder = QueryBuilders.termsQuery(field, values: _*)
  def boost(boost: Double): TermsQueryDefinition = {
    builder.boost(boost.toFloat)
    this
  }
  def minimumShouldMatch(minimumShouldMatch: Int): TermsQueryDefinition = {
    builder.minimumMatch(minimumShouldMatch)
    this
  }
  def disableCoord(disableCoord: Boolean): TermsQueryDefinition = {
    builder.disableCoord(disableCoord)
    this
  }
}

class TopChildrenQueryDefinition(`type`: String, q: QueryDefinition) extends QueryDefinition {
  val builder = QueryBuilders.topChildrenQuery(`type`, q.builder)
  def boost(boost: Double): TopChildrenQueryDefinition = {
    builder.boost(boost.toFloat)
    this
  }
  def factor(factor: Int): TopChildrenQueryDefinition = {
    builder.factor(factor)
    this
  }
  def incrementalFactor(incrementalFactor: Int): TopChildrenQueryDefinition = {
    builder.incrementalFactor(incrementalFactor)
    this
  }
  def score(score: String): TopChildrenQueryDefinition = {
    builder.score(score)
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

class MatchQueryDefinition(field: String, value: Any)
  extends QueryDefinition
  with DefinitionAttributeFuzziness
  with DefinitionAttributeFuzzyRewrite
  with DefinitionAttributePrefixLength
  with DefinitionAttributeRewrite {

  val builder = QueryBuilders.matchQuery(field, value)
  val _builder = builder

  def operator(op: String): MatchQueryDefinition = {
    op match {
      case "AND" => builder.operator(org.elasticsearch.index.query.MatchQueryBuilder.Operator.AND)
      case _ => builder.operator(org.elasticsearch.index.query.MatchQueryBuilder.Operator.OR)
    }
    this
  }

  def analyzer(a: Analyzer): MatchQueryDefinition = {
    builder.analyzer(a.name)
    this
  }

  def boost(boost: Double): MatchQueryDefinition = {
    builder.boost(boost.toFloat)
    this
  }

  def zeroTermsQuery(z: MatchQueryBuilder.ZeroTermsQuery) = {
    builder.zeroTermsQuery(z)
    this
  }

  def slop(s: Int) = {
    builder.slop(s)
    this
  }

  def setLenient(lenient: Boolean) = {
    builder.setLenient(lenient)
    this
  }

  def operator(op: MatchQueryBuilder.Operator) = {
    builder.operator(op)
    this
  }

  def minimumShouldMatch(a: Any) = {
    builder.minimumShouldMatch(a.toString)
    this
  }

  def maxExpansions(max: Int) = {
    builder.maxExpansions(max)
    this
  }

  def fuzzyTranspositions(f: Boolean): MatchQueryDefinition = {
    builder.fuzzyTranspositions(f)
    this
  }



  def cutoffFrequency(cutoff: Double): MatchQueryDefinition = {
    builder.cutoffFrequency(cutoff.toFloat)
    this
  }
}

class MatchPhrasePrefixDefinition(field: String, value: Any)
  extends QueryDefinition
  with DefinitionAttributeBoost
  with DefinitionAttributeFuzziness
  with DefinitionAttributeFuzzyRewrite
  with DefinitionAttributePrefixLength
  with DefinitionAttributeRewrite {

  def builder = _builder
  val _builder = QueryBuilders.matchPhrasePrefixQuery(field, value.toString)

  def analyzer(a: Analyzer): MatchPhrasePrefixDefinition = {
    builder.analyzer(a.name)
    this
  }

  def zeroTermsQuery(z: MatchQueryBuilder.ZeroTermsQuery): MatchPhrasePrefixDefinition = {
    builder.zeroTermsQuery(z)
    this
  }

  def slop(s: Int): MatchPhrasePrefixDefinition = {
    builder.slop(s)
    this
  }

  def setLenient(lenient: Boolean): MatchPhrasePrefixDefinition = {
    builder.setLenient(lenient)
    this
  }

  def operator(op: MatchQueryBuilder.Operator): MatchPhrasePrefixDefinition = {
    builder.operator(op)
    this
  }

  def operator(op: String): MatchPhrasePrefixDefinition = {
    op match {
      case "AND" => builder.operator(org.elasticsearch.index.query.MatchQueryBuilder.Operator.AND)
      case _ => builder.operator(org.elasticsearch.index.query.MatchQueryBuilder.Operator.OR)
    }
    this
  }

  def minimumShouldMatch(a: Any): MatchPhrasePrefixDefinition = {
    builder.minimumShouldMatch(a.toString)
    this
  }

  def maxExpansions(max: Int): MatchPhrasePrefixDefinition = {
    builder.maxExpansions(max)
    this
  }

  def fuzzyTranspositions(f: Boolean): MatchPhrasePrefixDefinition = {
    builder.fuzzyTranspositions(f)
    this
  }
  def cutoffFrequency(cutoff: Double): MatchPhrasePrefixDefinition = {
    builder.cutoffFrequency(cutoff.toFloat)
    this
  }
}

class MatchPhraseDefinition(field: String, value: Any)
  extends QueryDefinition
  with DefinitionAttributeFuzziness
  with DefinitionAttributeFuzzyRewrite
  with DefinitionAttributePrefixLength
  with DefinitionAttributeRewrite {

  val builder = QueryBuilders.matchPhraseQuery(field, value.toString)
  val _builder = builder

  def analyzer(a: Analyzer): MatchPhraseDefinition = {
    builder.analyzer(a.name)
    this
  }

  def boost(boost: Double): MatchPhraseDefinition = {
    builder.boost(boost.toFloat)
    this
  }

  def zeroTermsQuery(z: MatchQueryBuilder.ZeroTermsQuery) = {
    builder.zeroTermsQuery(z)
    this
  }

  def slop(s: Int): MatchPhraseDefinition = {
    builder.slop(s)
    this
  }

  def setLenient(lenient: Boolean): MatchPhraseDefinition = {
    builder.setLenient(lenient)
    this
  }

  def operator(op: MatchQueryBuilder.Operator): MatchPhraseDefinition = {
    builder.operator(op)
    this
  }

  def operator(op: String): MatchPhraseDefinition = {
    op match {
      case "AND" => builder.operator(org.elasticsearch.index.query.MatchQueryBuilder.Operator.AND)
      case _ => builder.operator(org.elasticsearch.index.query.MatchQueryBuilder.Operator.OR)
    }
    this
  }

  def minimumShouldMatch(a: Any) = {
    builder.minimumShouldMatch(a.toString)
    this
  }

  def maxExpansions(max: Int) = {
    builder.maxExpansions(max)
    this
  }

  def fuzzyTranspositions(f: Boolean) = {
    builder.fuzzyTranspositions(f)
    this
  }

  def cutoffFrequency(cutoff: Double) = {
    builder.cutoffFrequency(cutoff.toFloat)
    this
  }
}

class SimpleStringQueryDefinition(query: String) extends QueryDefinition {
  val builder = QueryBuilders.simpleQueryString(query)

  def defaultOperator(d: SimpleQueryStringBuilder.Operator): SimpleStringQueryDefinition = {
    builder.defaultOperator(d)
    this
  }

  def fields(fields: String*): SimpleStringQueryDefinition = {
    fields foreach field
    this
  }

  def field(name: String): SimpleStringQueryDefinition = {
    builder.field(name)
    this
  }

  def field(name: String, boost: Double): SimpleStringQueryDefinition = {
    builder.field(name, boost.toFloat)
    this
  }
}

class StringQueryDefinition(query: String) extends QueryDefinition with DefinitionAttributeRewrite {

  val builder = QueryBuilders.queryString(query)
  val _builder = builder

  def operator(op: String): StringQueryDefinition = {
    op.toLowerCase match {
      case "and" => builder.defaultOperator(QueryStringQueryBuilder.Operator.AND)
      case _ => builder.defaultOperator(QueryStringQueryBuilder.Operator.OR)
    }
    this
  }

  def fuzzyMaxExpansions(fuzzyMaxExpansions: Int): StringQueryDefinition = {
    builder.fuzzyMaxExpansions(fuzzyMaxExpansions)
    this
  }

  def lenient(l: Boolean): StringQueryDefinition = {
    builder.lenient(java.lang.Boolean.valueOf(l))
    this
  }

  def phraseSlop(phraseSlop: Int): StringQueryDefinition = {
    builder.phraseSlop(phraseSlop)
    this
  }

  def tieBreaker(tieBreaker: Double): StringQueryDefinition = {
    builder.tieBreaker(tieBreaker.toFloat)
    this
  }

  def fuzzyPrefixLength(fuzzyPrefixLength: Int): StringQueryDefinition = {
    builder.fuzzyPrefixLength(fuzzyPrefixLength)
    this
  }

  def fuzzyMinSim(fuzzyMinSim: Double): StringQueryDefinition = {
    builder.fuzzyMinSim(fuzzyMinSim.toFloat)
    this
  }

  def anaylyzer(analyzer: Analyzer): StringQueryDefinition = {
    builder.analyzer(analyzer.name)
    this
  }

  def defaultField(field: String): StringQueryDefinition = {
    builder.defaultField(field)
    this
  }

  def analyzeWildcard(analyzeWildcard: Boolean): StringQueryDefinition = {
    builder.analyzeWildcard(analyzeWildcard)
    this
  }

  def autoGeneratePhraseQueries(autoGeneratePhraseQueries: Boolean): StringQueryDefinition = {
    builder.autoGeneratePhraseQueries(autoGeneratePhraseQueries)
    this
  }

  def allowLeadingWildcard(allowLeadingWildcard: Boolean): StringQueryDefinition = {
    builder.allowLeadingWildcard(allowLeadingWildcard)
    this
  }

  def enablePositionIncrements(enablePositionIncrements: Boolean): StringQueryDefinition = {
    builder.enablePositionIncrements(enablePositionIncrements)
    this
  }

  def boost(boost: Double): StringQueryDefinition = {
    builder.boost(boost.toFloat)
    this
  }

  def field(name: String): StringQueryDefinition = {
    builder.field(name)
    this
  }

  def field(name: String, boost: Double): StringQueryDefinition = {
    builder.field(name, boost.toFloat)
    this
  }
}

class NestedQueryDefinition(path: String) extends QueryDefinition {
  private var _query: QueryDefinition = _
  private var _boost: Double = 1.0
  private var _scoreMode: String = _

  def builder = {
    require(_query != null, "must specify query for nested score query")
    QueryBuilders.nestedQuery(path, _query.builder).scoreMode(_scoreMode).boost(_boost.toFloat)
  }

  def query(query: QueryDefinition): NestedQueryDefinition = {
    _query = query
    this
  }

  def scoreMode(scoreMode: String): NestedQueryDefinition = {
    _scoreMode = scoreMode
    this
  }

  def boost(b: Double): NestedQueryDefinition = {
    _boost = b
    this
  }

}