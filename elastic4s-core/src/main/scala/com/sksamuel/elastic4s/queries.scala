package com.sksamuel.elastic4s

import org.elasticsearch.index.query._
import org.elasticsearch.index.query.CommonTermsQueryBuilder.Operator
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder
import com.sksamuel.elastic4s.DefinitionAttributes._
import org.elasticsearch.common.unit.Fuzziness
import org.elasticsearch.index.query.support.QueryInnerHitBuilder
import org.elasticsearch.search.fetch.innerhits.InnerHitsBuilder.InnerHit

import scala.language.implicitConversions
import scala.util.{Right, Left}

/** @author Stephen Samuel */

trait QueryDsl {

  implicit def string2query(string: String): SimpleStringQueryDefinition = new SimpleStringQueryDefinition(string)
  implicit def tuple2query(kv: (String, String)): TermQueryDefinition = new TermQueryDefinition(kv._1, kv._2)

  def query = this

  @deprecated("use boostingQuery", "1.4.0")
  def boosting: BoostingQueryDefinition = boostingQuery
  def boostingQuery: BoostingQueryDefinition = new BoostingQueryDefinition

  def commonQuery(field: String) = new CommonQueryExpectsText(field)
  def commonQuery = new CommonQueryExpectsField
  class CommonQueryExpectsField {
    def field(name: String) = new CommonQueryExpectsText(name)
  }
  class CommonQueryExpectsText(name: String) {
    def text(q: String): CommonTermsQueryDefinition = new CommonTermsQueryDefinition(name, q)
    def query(q: String): CommonTermsQueryDefinition = text(q)
  }

  @deprecated("use constantScoreQuery or constantScoreFilter to be consistent with other query type syntax", "1.6.5")
  def constantScore = new ConstantScoreExpectsQueryOrFilter
  class ConstantScoreExpectsQueryOrFilter {
    @deprecated("use constantScoreQuery or constantScoreFilter to be consistent with other query type syntax", "1.6.5")
    def query(query: QueryDefinition) = new ConstantScoreDefinition(QueryBuilders.constantScoreQuery(query.builder))
    @deprecated("use constantScoreQuery or constantScoreFilter to be consistent with other query type syntax", "1.6.5")
    def filter(filter: FilterDefinition) = new ConstantScoreDefinition(QueryBuilders.constantScoreQuery(filter.builder))
  }

  def constantScoreQuery(q: QueryDefinition) = constantScore query q
  def constantScoreFilter(f: FilterDefinition) = constantScore filter f

  def dismax = new DisMaxDefinition

  @deprecated("deprecated by elasticsearch", "1.6.5")
  def fuzzylikethis: FuzzyLikeThisDefinitionExpectsText = flt

  @deprecated("deprecated by elasticsearch", "1.6.5")
  def flt: FuzzyLikeThisDefinitionExpectsText = new FuzzyLikeThisDefinitionExpectsText

  @deprecated("deprecated by elasticsearch", "1.6.5")
  def flt(text: String): FuzzyLikeThisExpectsField = new FuzzyLikeThisExpectsField(text)

  @deprecated("deprecated by elasticsearch", "1.6.5")
  def fuzzylikethis(text: String): FuzzyLikeThisExpectsField = flt(text)

  @deprecated("deprecated by elasticsearch", "1.6.5")
  class FuzzyLikeThisDefinitionExpectsText {
    def text(q: String) = new FuzzyLikeThisExpectsField(q)
  }

  @deprecated("deprecated by elasticsearch", "1.6.5")
  class FuzzyLikeThisExpectsField(text: String) {
    def field(name: String): FuzzyLikeThisDefinition = fields(name)
    def fields(names: String*): FuzzyLikeThisDefinition = new FuzzyLikeThisDefinition(text, names)
  }

  def functionScoreQuery(query: QueryDefinition): FunctionScoreQueryDefinition = new
      FunctionScoreQueryDefinition(Left(query))
  def functionScoreQuery(filter: FilterDefinition): FunctionScoreQueryDefinition = new
      FunctionScoreQueryDefinition(Right(filter))

  def filteredQuery = new FilteredQueryDefinition

  @deprecated("use fuzzyQuery", "1.4.0")
  def fuzzy(name: String, value: Any) = fuzzyQuery(name, value)
  def fuzzyQuery(name: String, value: Any) = new FuzzyQueryDefinition(name, value)

  def indicesQuery(indices: String*) = new {
    def query(query: QueryDefinition): IndicesQueryDefinition = new IndicesQueryDefinition(indices, query)
  }

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

  @deprecated("use matchQuery", "1.6.5")
  def matches(tuple: (String, Any)): MatchQueryDefinition = matchQuery(tuple)
  @deprecated("use matchQuery", "1.6.5")
  def matches(field: String, value: Any): MatchQueryDefinition = matchQuery(field, value)

  def matchQuery(tuple: (String, Any)): MatchQueryDefinition = matchQuery(tuple._1, tuple._2)
  def matchQuery(field: String, value: Any): MatchQueryDefinition = new MatchQueryDefinition(field, value)

  @deprecated("use matchPhraseQuery", "1.6.5")
  def matchPhrase(field: String, value: Any): MatchPhraseDefinition = new MatchPhraseDefinition(field, value)
  def matchPhraseQuery(field: String, value: Any): MatchPhraseDefinition = new MatchPhraseDefinition(field, value)

  @deprecated("use matchPhrasePrefixQuery", "1.6.5")
  def matchPhrasePrefix(field: String, value: Any) = new MatchPhrasePrefixDefinition(field, value)
  def matchPhrasePrefixQuery(field: String, value: Any) = new MatchPhrasePrefixDefinition(field, value)

  def multiMatchQuery(text: String) = new MultiMatchQueryDefinition(text)

  @deprecated("use matchAllQuery", "1.6.5")
  def matchall = new MatchAllQueryDefinition
  def matchAllQuery = new MatchAllQueryDefinition

  def morelikeThisQuery(fields: String*) = new MoreLikeThisQueryDefinition(fields: _*)

  @deprecated("use nestedQuery", "1.4.0")
  def nested(path: String): NestedQueryDefinition = nestedQuery(path)
  def nestedQuery(path: String): NestedQueryDefinition = new NestedQueryDefinition(path)

  def query(q: String): QueryStringQueryDefinition = queryStringQuery(q)
  def queryStringQuery(q: String): QueryStringQueryDefinition = new QueryStringQueryDefinition(q)

  @deprecated("use rangeQuery", "1.4.0")
  def range(field: String): RangeQueryDefinition = rangeQuery(field)
  def rangeQuery(field: String): RangeQueryDefinition = new RangeQueryDefinition(field)

  @deprecated("use regexQuery", "1.4.0")
  def regex(tuple: (String, Any)): RegexQueryDefinition = regexQuery(tuple._1, tuple._2)
  @deprecated("use regexQuery", "1.4.0")
  def regex(field: String, value: Any): RegexQueryDefinition = regexQuery(field, value)
  def regexQuery(tuple: (String, Any)): RegexQueryDefinition = regexQuery(tuple._1, tuple._2)
  def regexQuery(field: String, value: Any): RegexQueryDefinition = new RegexQueryDefinition(field, value)

  @deprecated("use prefixQuery", "1.4.0")
  def prefix(tuple: (String, Any)): PrefixQueryDefinition = prefixQuery(tuple)
  @deprecated("use prefixQuery", "1.4.0")
  def prefix(field: String, value: Any): PrefixQueryDefinition = prefixQuery(field, value)
  def prefixQuery(tuple: (String, Any)): PrefixQueryDefinition = prefixQuery(tuple._1, tuple._2)
  def prefixQuery(field: String, value: Any): PrefixQueryDefinition = new PrefixQueryDefinition(field, value)

  def simpleStringQuery(q: String): SimpleStringQueryDefinition = new SimpleStringQueryDefinition(q)
  def stringQuery(q: String): QueryStringQueryDefinition = new QueryStringQueryDefinition(q)

  def spanFirstQuery = new {
    def query(spanQuery: SpanQueryDefinition) = new {
      def end(end: Int) = new SpanFirstQueryDefinition(spanQuery, end)
    }
  }

  def spanOrQuery: SpanOrQueryDefinition = new SpanOrQueryDefinition
  def spanTermQuery(field: String, value: Any): SpanTermQueryDefinition = new SpanTermQueryDefinition(field, value)
  def spanNotQuery: SpanNotQueryDefinition = new SpanNotQueryDefinition
  def spanNearQuery: SpanNearQueryDefinition = new SpanNearQueryDefinition

  def spanMultiTermQuery(query: MultiTermQueryDefinition): SpanMultiTermQueryDefinition = new
      SpanMultiTermQueryDefinition(query)

  @deprecated("use termQuery", "1.4.0")
  def term(tuple: (String, Any)): TermQueryDefinition = termQuery(tuple)
  @deprecated("use termQuery", "1.4.0")
  def term(field: String, value: Any): TermQueryDefinition = termQuery(field, value)
  def termQuery(tuple: (String, Any)): TermQueryDefinition = termQuery(tuple._1, tuple._2)
  def termQuery(field: String, value: Any): TermQueryDefinition = new TermQueryDefinition(field, value)

  def termsQuery(field: String, values: AnyRef*): TermsQueryDefinition =
    new TermsQueryDefinition(field, values.map(_.toString): _*)

  @deprecated("deprecated by elasticsearch", "1.6.5")
  def topChildren(`type`: String) = new TopChildrenExpectsQuery(`type`)
  class TopChildrenExpectsQuery(`type`: String) {
    @deprecated("deprecated by elasticsearch", "1.6.5")
    def query(q: QueryDefinition): TopChildrenQueryDefinition = new TopChildrenQueryDefinition(`type`, q)
  }

  @deprecated("use wildcardQuery", "1.4.0")
  def wildcard(tuple: (String, Any)): WildcardQueryDefinition = wildcardQuery(tuple)
  @deprecated("use wildcardQuery", "1.4.0")
  def wildcard(field: String, value: Any): WildcardQueryDefinition = wildcardQuery(field, value)
  def wildcardQuery(tuple: (String, Any)): WildcardQueryDefinition = wildcardQuery(tuple._1, tuple._2)
  def wildcardQuery(field: String, value: Any): WildcardQueryDefinition = new WildcardQueryDefinition(field, value)

  def ids(iterable: Iterable[String]): IdQueryDefinition = ids(iterable.toSeq: _*)
  def ids(ids: String*): IdQueryDefinition = new IdQueryDefinition(ids: _*)
  def all: MatchAllQueryDefinition = new MatchAllQueryDefinition

  def bool(block: => BoolQueryDefinition): BoolQueryDefinition = block
  def must(queries: QueryDefinition*): BoolQueryDefinition = new BoolQueryDefinition().must(queries: _*)
  def must(queries: Iterable[QueryDefinition]): BoolQueryDefinition = new BoolQueryDefinition().must(queries)
  def should(queries: QueryDefinition*): BoolQueryDefinition = new BoolQueryDefinition().should(queries: _*)
  def should(queries: Iterable[QueryDefinition]): BoolQueryDefinition = new BoolQueryDefinition().should(queries)
  def not(queries: QueryDefinition*): BoolQueryDefinition = new BoolQueryDefinition().not(queries: _*)
  def not(queries: Iterable[QueryDefinition]): BoolQueryDefinition = new BoolQueryDefinition().not(queries)
}

class BoolQueryDefinition extends QueryDefinition {
  val builder = QueryBuilders.boolQuery()
  def adjustPureNegative(adjustPureNegative: Boolean): this.type = {
    builder.adjustPureNegative(adjustPureNegative)
    this
  }
  def boost(boost: Double): this.type = {
    builder.boost(boost.toFloat)
    this
  }
  def must(queries: QueryDefinition*): this.type = {
    queries.foreach(builder must _.builder)
    this
  }
  def must(queries: Iterable[QueryDefinition]): this.type = {
    queries.foreach(builder must _.builder)
    this
  }
  def not(queries: QueryDefinition*): this.type = {
    queries.foreach(builder mustNot _.builder)
    this
  }
  def not(queries: Iterable[QueryDefinition]): this.type = {
    queries.foreach(builder mustNot _.builder)
    this
  }
  def should(queries: QueryDefinition*): this.type = {
    queries.foreach(builder should _.builder)
    this
  }
  def should(queries: Iterable[QueryDefinition]): this.type = {
    queries.foreach(builder should _.builder)
    this
  }
  def minimumShouldMatch(minimumShouldMatch: String): this.type = {
    builder.minimumShouldMatch(minimumShouldMatch: String)
    this
  }
  def minimumShouldMatch(minimumNumberShouldMatch: Int): this.type = {
    builder.minimumNumberShouldMatch(minimumNumberShouldMatch: Int)
    this
  }
  def disableCoord(disableCoord: Boolean): this.type = {
    builder.disableCoord(disableCoord: Boolean)
    this
  }
  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}

trait QueryDefinition {
  def builder: org.elasticsearch.index.query.QueryBuilder
}

class FunctionScoreQueryDefinition(queryOrFilter: Either[QueryDefinition, FilterDefinition])
  extends QueryDefinition
  with DefinitionAttributeBoost
  with DefinitionAttributeBoostMode
  with DefinitionAttributeMaxBoost
  with DefinitionAttributeScoreMode {

  val builder = queryOrFilter match {
    case Left(query) => new FunctionScoreQueryBuilder(query.builder)
    case Right(filter) => new FunctionScoreQueryBuilder(filter.builder)
  }
  val _builder = builder

  def scorers(scorers: ScoreDefinition[_]*): FunctionScoreQueryDefinition = {
    scorers.foreach(scorer => scorer._filter match {
      case None => builder.add(scorer.builder)
      case Some(filter) => builder.add(filter.builder, scorer.builder)
    })
    this
  }
}

class MoreLikeThisQueryDefinition(fields: String*) extends QueryDefinition {

  val _builder = QueryBuilders.moreLikeThisQuery(fields: _*)
  val builder = _builder

  def analyzer(analyser: String): this.type = {
    _builder.analyzer(analyser)
    this
  }

  def analyser(analyser: String): this.type = {
    _builder.analyzer(analyser)
    this
  }

  def ids(ids: String*): this.type = {
    _builder.ids(ids: _*)
    this
  }

  def exclude(): this.type = {
    _builder.include(false)
    this
  }

  def include(): this.type = {
    _builder.include(true)
    this
  }

  def failOnUnsupportedField(): this.type = {
    _builder.failOnUnsupportedField(true)
    this
  }

  def notFailOnUnsupportedField(): this.type = {
    _builder.failOnUnsupportedField(false)
    this
  }

  def likeText(text: String): this.type = {
    _builder.likeText(text)
    this
  }

  def minTermFreq(freq: Int): this.type = {
    _builder.minTermFreq(freq)
    this
  }

  def stopWords(stopWords: String*): this.type = {
    _builder.stopWords(stopWords: _*)
    this
  }

  @deprecated("deprecated by elasticsearch", "1.6.5")
  def percentTermsToMatch(percentTermsToMatch: Double): this.type = {
    _builder.percentTermsToMatch(percentTermsToMatch.toFloat)
    this
  }

  def maxWordLength(maxWordLen: Int): this.type = {
    _builder.maxWordLength(maxWordLen)
    this
  }

  def minWordLength(minWordLen: Int): this.type = {
    _builder.minWordLength(minWordLen)
    this
  }

  def boostTerms(boostTerms: Double): this.type = {
    _builder.boostTerms(boostTerms.toFloat)
    this
  }

  def boost(boost: Double): this.type = {
    _builder.boost(boost.toFloat)
    this
  }

  def maxQueryTerms(maxQueryTerms: Int): this.type = {
    _builder.maxQueryTerms(maxQueryTerms)
    this
  }

  def minDocFreq(minDocFreq: Int): this.type = {
    _builder.minDocFreq(minDocFreq)
    this
  }

  def maxDocFreq(maxDocFreq: Int): this.type = {
    _builder.maxDocFreq(maxDocFreq)
    this
  }
}

class MultiMatchQueryDefinition(text: String)
  extends QueryDefinition
  with DefinitionAttributeFuzziness
  with DefinitionAttributePrefixLength
  with DefinitionAttributeFuzzyRewrite
  with DefinitionAttributeCutoffFrequency {

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

  def minimumShouldMatch(minimumShouldMatch: String): MultiMatchQueryDefinition = {
    builder.minimumShouldMatch(minimumShouldMatch: String)
    this
  }

  @deprecated("@deprecated use a tieBreaker of 1.0f to disable dis-max query or select the appropriate Type", "1.2.0")
  def useDisMax(useDisMax: Boolean): MultiMatchQueryDefinition = {
    builder.useDisMax(java.lang.Boolean.valueOf(useDisMax))
    this
  }

  def lenient(l: Boolean): MultiMatchQueryDefinition = {
    builder.lenient(l)
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

  def operator(op: MatchQueryBuilder.Operator): MultiMatchQueryDefinition = {
    builder.operator(op)
    this
  }

  def operator(op: String): MultiMatchQueryDefinition = {
    op match {
      case "AND" => builder.operator(org.elasticsearch.index.query.MatchQueryBuilder.Operator.AND)
      case _ => builder.operator(org.elasticsearch.index.query.MatchQueryBuilder.Operator.OR)
    }
    this
  }

  def matchType(t: MultiMatchQueryBuilder.Type): MultiMatchQueryDefinition = {
    builder.`type`(t)
    this
  }

  def matchType(t: String): MultiMatchQueryDefinition = {
    val mt = t match {
      case "most_fields" => MultiMatchQueryBuilder.Type.MOST_FIELDS
      case "cross_fields" => MultiMatchQueryBuilder.Type.CROSS_FIELDS
      case "phrase" => MultiMatchQueryBuilder.Type.PHRASE
      case "phrase_prefix" => MultiMatchQueryBuilder.Type.PHRASE_PREFIX
      case _ => MultiMatchQueryBuilder.Type.BEST_FIELDS
    }

    matchType(mt)
  }
}

class SpanMultiTermQueryDefinition(query: MultiTermQueryDefinition) extends SpanQueryDefinition {
  override val builder = QueryBuilders.spanMultiTermQueryBuilder(query.builder)
}

class FuzzyQueryDefinition(name: String, value: Any)
  extends MultiTermQueryDefinition
  with DefinitionAttributePrefixLength
  with DefinitionAttributeBoost {

  val builder = QueryBuilders.fuzzyQuery(name, value.toString)
  val _builder = builder

  def fuzziness(fuzziness: Fuzziness) = {
    builder.fuzziness(fuzziness)
    this
  }

  def maxExpansions(maxExpansions: Int) = {
    builder.maxExpansions(maxExpansions)
    this
  }
  def transpositions(transpositions: Boolean) = {
    builder.transpositions(transpositions)
    this
  }
}

class HasChildQueryDefinition(`type`: String, q: QueryDefinition)
  extends QueryDefinition with DefinitionAttributeBoost {
  val builder = QueryBuilders.hasChildQuery(`type`, q.builder)
  val _builder = builder
  def scoreType(scoreType: String): HasChildQueryDefinition = {
    builder.scoreType(scoreType)
    this
  }
}

class HasParentQueryDefinition(`type`: String, q: QueryDefinition)
  extends QueryDefinition with DefinitionAttributeBoost {
  val builder = QueryBuilders.hasParentQuery(`type`, q.builder)
  val _builder = builder
  def scoreType(scoreType: String): HasParentQueryDefinition = {
    builder.scoreType(scoreType)
    this
  }
}

class IndicesQueryDefinition(indices: Iterable[String], query: QueryDefinition) extends QueryDefinition {
  override val builder = QueryBuilders.indicesQuery(query.builder, indices.toSeq: _*)
  def noMatchQuery(query: QueryDefinition): this.type = {
    builder.noMatchQuery(query.builder)
    this
  }
  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}

class ConstantScoreDefinition(val builder: ConstantScoreQueryBuilder) extends QueryDefinition {
  def boost(b: Double): QueryDefinition = {
    builder.boost(b.toFloat)
    this
  }
}

@deprecated("deprecated by elasticsearch", "1.6.5")
class FuzzyLikeThisDefinition(text: String, fields: Iterable[String])
  extends QueryDefinition
  with DefinitionAttributePrefixLength
  with DefinitionAttributeBoost {

  val builder = fields.size match {
    case 0 => QueryBuilders.fuzzyLikeThisQuery().likeText(text)
    case _ => QueryBuilders.fuzzyLikeThisQuery(fields.toSeq: _*).likeText(text)
  }
  val _builder = builder

  @deprecated("deprecated by elasticsearch", "1.6.5")
  def analyzer(a: Analyzer): FuzzyLikeThisDefinition = {
    builder.analyzer(a.name)
    this
  }

  @deprecated("deprecated by elasticsearch", "1.6.5")
  def ignoreTF(b: Boolean): FuzzyLikeThisDefinition = {
    builder.ignoreTF(b)
    this
  }

  @deprecated("deprecated by elasticsearch", "1.6.5")
  def maxQueryTerms(b: Int): FuzzyLikeThisDefinition = {
    builder.maxQueryTerms(b)
    this
  }

  @deprecated("deprecated by elasticsearch", "1.6.5")
  def fuzziness(f: Fuzziness): this.type = {
    _builder.fuzziness(f)
    this
  }

  @deprecated("deprecated by elasticsearch", "1.6.5")
  def failOnUnsupportedField(failOnUnsupportedField: Boolean): FuzzyLikeThisDefinition = {
    builder.failOnUnsupportedField(failOnUnsupportedField)
    this
  }
}

class CommonTermsQueryDefinition(name: String, text: String)
  extends QueryDefinition
  with DefinitionAttributeBoost
  with DefinitionAttributeCutoffFrequency {

  val builder = QueryBuilders.commonTermsQuery(name, text)
  val _builder = builder

  def highFreqMinimumShouldMatch(highFreqMinimumShouldMatch: Int): CommonTermsQueryDefinition = {
    builder.highFreqMinimumShouldMatch(highFreqMinimumShouldMatch.toString)
    this
  }
  def highFreqOperator(operator: String): CommonTermsQueryDefinition = {
    builder.highFreqOperator(if (operator.toLowerCase == "and") Operator.AND else Operator.OR)
    this
  }
  def analyzer(analyzer: Analyzer): CommonTermsQueryDefinition = {
    builder.analyzer(analyzer.name)
    this
  }
  def lowFreqMinimumShouldMatch(lowFreqMinimumShouldMatch: Int): CommonTermsQueryDefinition = {
    builder.lowFreqMinimumShouldMatch(lowFreqMinimumShouldMatch.toString)
    this
  }
  def lowFreqOperator(operator: String): CommonTermsQueryDefinition = {
    builder.lowFreqOperator(if (operator.toLowerCase == "and") Operator.AND else Operator.OR)
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
  private var _query: QueryBuilder = QueryBuilders.matchAllQuery
  private var _filter: FilterBuilder = null
  private var _boost: Double = -1d
  def boost(boost: Double): FilteredQueryDefinition = {
    _boost = boost
    this
  }
  def query(query: => QueryDefinition): FilteredQueryDefinition = {
    _query = Option(query).map(_.builder).getOrElse(_query)
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

class SpanOrQueryDefinition extends SpanQueryDefinition with DefinitionAttributeBoost {
  val builder = QueryBuilders.spanOrQuery
  val _builder = builder
  def clause(spans: SpanTermQueryDefinition*): SpanOrQueryDefinition = {
    spans.foreach {
      span => builder.clause(span.builder)
    }
    this
  }
}

class SpanTermQueryDefinition(field: String, value: Any) extends SpanQueryDefinition {
  val builder = QueryBuilders.spanTermQuery(field, value.toString)
  def boost(boost: Double) = {
    builder.boost(boost.toFloat)
    this
  }
}

trait MultiTermQueryDefinition extends QueryDefinition {
  override def builder: MultiTermQueryBuilder
}

class WildcardQueryDefinition(field: String, query: Any)
  extends QueryDefinition
  with DefinitionAttributeRewrite
  with DefinitionAttributeBoost {
  val builder = QueryBuilders.wildcardQuery(field, query.toString)
  val _builder = builder
}

class PrefixQueryDefinition(field: String, prefix: Any)
  extends MultiTermQueryDefinition
  with DefinitionAttributeRewrite
  with DefinitionAttributeBoost {
  val builder = QueryBuilders.prefixQuery(field, prefix.toString)
  val _builder = builder
}

class RegexQueryDefinition(field: String, regex: Any)
  extends MultiTermQueryDefinition
  with DefinitionAttributeRewrite
  with DefinitionAttributeBoost {
  val builder = QueryBuilders.regexpQuery(field, regex.toString)
  val _builder = builder
  def flags(flags: RegexpFlag*): RegexQueryDefinition = {
    builder.flags(flags: _*)
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

trait SpanQueryDefinition extends QueryDefinition {
  override def builder: SpanQueryBuilder
}

class SpanFirstQueryDefinition(query: SpanQueryDefinition, end: Int) extends QueryDefinition {
  val builder = QueryBuilders.spanFirstQuery(query.builder, end)
}

class SpanNotQueryDefinition extends QueryDefinition {
  val builder = QueryBuilders.spanNotQuery()
  def boost(boost: Double): this.type = {
    builder.boost(boost.toFloat)
    this
  }
  def dist(dist: Int): this.type = {
    builder.dist(dist)
    this
  }
  def exclude(query: SpanQueryDefinition): this.type = {
    builder.exclude(query.builder)
    this
  }
  def include(query: SpanQueryDefinition): this.type = {
    builder.include(query.builder)
    this
  }
  def pre(pre: Int): this.type = {
    builder.pre(pre)
    this
  }
  def post(post: Int): this.type = {
    builder.post(post)
    this
  }
}

class SpanNearQueryDefinition extends SpanQueryDefinition {
  val builder = QueryBuilders.spanNearQuery()
  def boost(boost: Double): this.type = {
    builder.boost(boost.toFloat)
    this
  }
  def inOrder(inOrder: Boolean): this.type = {
    builder.inOrder(inOrder)
    this
  }
  def collectPayloads(collectPayloads: Boolean): this.type = {
    builder.collectPayloads(collectPayloads)
    this
  }
  def clause(query: SpanQueryDefinition): this.type = {
    builder.clause(query.builder)
    this
  }
  def slop(slop: Int): this.type = {
    builder.slop(slop)
    this
  }
}

class TopChildrenQueryDefinition(`type`: String, q: QueryDefinition)
  extends QueryDefinition
  with DefinitionAttributeBoost {

  val builder = QueryBuilders.topChildrenQuery(`type`, q.builder)
  val _builder = builder

  @deprecated("deprecated by elasticsearch", "1.6.5")
  def factor(factor: Int): TopChildrenQueryDefinition = {
    builder.factor(factor)
    this
  }

  @deprecated("deprecated by elasticsearch", "1.6.5")
  def incrementalFactor(incrementalFactor: Int): TopChildrenQueryDefinition = {
    builder.incrementalFactor(incrementalFactor)
    this
  }

  @deprecated("deprecated by elasticsearch", "1.6.5")
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

class RangeQueryDefinition(field: String) extends MultiTermQueryDefinition with DefinitionAttributeBoost {

  val builder = QueryBuilders.rangeQuery(field)
  val _builder = builder

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
}

class MatchQueryDefinition(field: String, value: Any)
  extends QueryDefinition
  with DefinitionAttributeBoost
  with DefinitionAttributeFuzziness
  with DefinitionAttributeFuzzyRewrite
  with DefinitionAttributePrefixLength
  with DefinitionAttributeRewrite
  with DefinitionAttributeCutoffFrequency {

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
}

class MatchPhrasePrefixDefinition(field: String, value: Any)
  extends QueryDefinition
  with DefinitionAttributeBoost
  with DefinitionAttributeFuzziness
  with DefinitionAttributeFuzzyRewrite
  with DefinitionAttributePrefixLength
  with DefinitionAttributeRewrite
  with DefinitionAttributeCutoffFrequency {

  def builder = _builder
  val _builder = QueryBuilders.matchPhrasePrefixQuery(field, value.toString)

  def analyzer(a: Analyzer): MatchPhrasePrefixDefinition = {
    builder.analyzer(a.name)
    this
  }

  def analyzer(name: String): MatchPhrasePrefixDefinition = {
    builder.analyzer(name)
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
}

class MatchPhraseDefinition(field: String, value: Any)
  extends QueryDefinition
  with DefinitionAttributeBoost
  with DefinitionAttributeFuzziness
  with DefinitionAttributeFuzzyRewrite
  with DefinitionAttributePrefixLength
  with DefinitionAttributeRewrite
  with DefinitionAttributeCutoffFrequency {

  val builder = QueryBuilders.matchPhraseQuery(field, value.toString)
  val _builder = builder

  def analyzer(a: Analyzer): MatchPhraseDefinition = {
    builder.analyzer(a.name)
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

}

class SimpleStringQueryDefinition(query: String) extends QueryDefinition {

  val builder = QueryBuilders.simpleQueryStringQuery(query)

  def analyzer(analyzer: String): SimpleStringQueryDefinition = {
    builder.analyzer(analyzer)
    this
  }

  def analyzer(analyzer: Analyzer): SimpleStringQueryDefinition = {
    builder.analyzer(analyzer.name)
    this
  }

  def defaultOperator(op: String): SimpleStringQueryDefinition = {
    op match {
      case "AND" => builder.defaultOperator(SimpleQueryStringBuilder.Operator.AND)
      case _ => builder.defaultOperator(SimpleQueryStringBuilder.Operator.OR)
    }
    this
  }

  def defaultOperator(d: SimpleQueryStringBuilder.Operator): SimpleStringQueryDefinition = {
    builder.defaultOperator(d)
    this
  }

  def asfields(fields: String*): SimpleStringQueryDefinition = {
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

  def flags(flags: SimpleQueryStringFlag*): SimpleStringQueryDefinition = {
    builder.flags(flags: _*)
    this
  }
}

class QueryStringQueryDefinition(query: String)
  extends QueryDefinition
  with DefinitionAttributeRewrite
  with DefinitionAttributeBoost {

  val builder = QueryBuilders.queryStringQuery(query)
  val _builder = builder

  def analyzer(analyzer: String): this.type = {
    builder.analyzer(analyzer)
    this
  }

  def analyzer(analyzer: Analyzer): this.type = {
    builder.analyzer(analyzer.name)
    this
  }

  def defaultOperator(op: String): this.type = {
    op.toUpperCase match {
      case "AND" => builder.defaultOperator(QueryStringQueryBuilder.Operator.AND)
      case _ => builder.defaultOperator(QueryStringQueryBuilder.Operator.OR)
    }
    this
  }

  def defaultOperator(op: QueryStringQueryBuilder.Operator): this.type = {
    builder.defaultOperator(op)
    this
  }

  def asfields(fields: String*): this.type = {
    fields foreach field
    this
  }

  def lowercaseExpandedTerms(lowercaseExpandedTerms: Boolean): this.type = {
    builder.lowercaseExpandedTerms(lowercaseExpandedTerms)
    this
  }

  def fuzzyPrefixLength(fuzzyPrefixLength: Int): this.type = {
    builder.fuzzyPrefixLength(fuzzyPrefixLength)
    this
  }

  def fuzzyMaxExpansions(fuzzyMaxExpansions: Int): this.type = {
    builder.fuzzyMaxExpansions(fuzzyMaxExpansions)
    this
  }

  def fuzzyRewrite(fuzzyRewrite: String): this.type = {
    builder.fuzzyRewrite(fuzzyRewrite)
    this
  }

  def tieBreaker(tieBreaker: Double): this.type = {
    builder.tieBreaker(tieBreaker.toFloat)
    this
  }

  def allowLeadingWildcard(allowLeadingWildcard: Boolean): this.type = {
    builder.allowLeadingWildcard(allowLeadingWildcard)
    this
  }

  def lenient(lenient: Boolean): this.type = {
    builder.lenient(lenient)
    this
  }

  def minimumShouldMatch(minimumShouldMatch: Int): this.type = {
    builder.minimumShouldMatch(minimumShouldMatch.toString)
    this
  }

  def enablePositionIncrements(enablePositionIncrements: Boolean): this.type = {
    builder.enablePositionIncrements(enablePositionIncrements)
    this
  }

  def quoteFieldSuffix(quoteFieldSuffix: String): this.type = {
    builder.quoteFieldSuffix(quoteFieldSuffix)
    this
  }

  def field(name: String): this.type = {
    builder.field(name)
    this
  }

  def field(name: String, boost: Double): this.type = {
    builder.field(name, boost.toFloat)
    this
  }

  def defaultField(field: String): this.type = {
    builder.defaultField(field)
    this
  }

  def analyzeWildcard(analyzeWildcard: Boolean): this.type = {
    builder.analyzeWildcard(analyzeWildcard)
    this
  }

  def autoGeneratePhraseQueries(autoGeneratePhraseQueries: Boolean): this.type = {
    builder.autoGeneratePhraseQueries(autoGeneratePhraseQueries)
    this
  }

  def operator(op: String): this.type = {
    op.toLowerCase match {
      case "and" => builder.defaultOperator(QueryStringQueryBuilder.Operator.AND)
      case _ => builder.defaultOperator(QueryStringQueryBuilder.Operator.OR)
    }
    this
  }

  def phraseSlop(phraseSlop: Int): QueryStringQueryDefinition = {
    builder.phraseSlop(phraseSlop)
    this
  }
}

class NestedQueryDefinition(path: String) extends QueryDefinition {
  private var _query: QueryDefinition = _
  private var _boost: Double = 1.0
  private var _scoreMode: String = _
  private var _inner: QueryInnerHitBuilder = _

  def builder = {
    require(_query != null, "must specify query for nested score query")
    QueryBuilders.nestedQuery(path, _query.builder).scoreMode(_scoreMode).boost(_boost.toFloat).innerHit(_inner)
  }

  def inner(name: String): this.type = {
    _inner = new QueryInnerHitBuilder().setName(name)
    this
  }

  def inner(inner: QueryInnerHitsDefinition): this.type = {
    _inner = inner.builder
    this
  }

  def query(query: QueryDefinition): this.type = {
    _query = query
    this
  }

  def scoreMode(scoreMode: String): this.type = {
    _scoreMode = scoreMode
    this
  }

  def boost(b: Double): this.type = {
    _boost = b
    this
  }
}

class QueryInnerHitsDefinition(private[elastic4s] val name: String) {

  private[elastic4s] val builder = new QueryInnerHitBuilder().setName(name)

  def from(f: Int): this.type = {
    builder.setFrom(f)
    this
  }

  def size(s: Int): this.type = {
    builder.setSize(s)
    this
  }
}

class InnerHitDefinition(private[elastic4s] val name: String) {

  private[elastic4s] val inner = new InnerHit

  def path(p: String): this.type = {
    inner.setPath(p)
    this
  }

  def `type`(t: String): this.type = {
    inner.setType(t)
    this
  }
}
