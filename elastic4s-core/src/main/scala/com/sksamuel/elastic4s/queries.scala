package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.DefinitionAttributes._
import com.sksamuel.elastic4s.anaylzers.Analyzer
import org.elasticsearch.common.geo.GeoDistance
import org.elasticsearch.common.unit.{DistanceUnit, Fuzziness}
import org.elasticsearch.index.query.CommonTermsQueryBuilder.Operator
import org.elasticsearch.index.query._
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder
import org.elasticsearch.index.query.support.QueryInnerHitBuilder
import org.elasticsearch.search.fetch.innerhits.InnerHitsBuilder.InnerHit

import scala.language.implicitConversions

/** @author Stephen Samuel */

trait QueryDsl {

  implicit def string2query(string: String): SimpleStringQueryDefinition = new SimpleStringQueryDefinition(string)
  implicit def tuple2query(kv: (String, String)): TermQueryDefinition = new TermQueryDefinition(kv._1, kv._2)

  def query = this

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

  def constantScoreQuery(q: QueryDefinition) = ConstantScoreDefinition(QueryBuilders.constantScoreQuery(q.builder))

  def dismax = new DisMaxDefinition

  def existsQuery = ExistsQueryDefinition

  def functionScoreQuery(query: QueryDefinition): FunctionScoreQueryDefinition = new FunctionScoreQueryDefinition(query)

  @deprecated("Use boolQuery instead with a must clause for the query and a filter clause for the filter", "2.0.0")
  def filteredQuery = new FilteredQueryDefinition

  def fuzzyQuery(name: String, value: Any) = new FuzzyQueryDefinition(name, value)

  def geoDistanceQuery(field: String): GeoDistanceQueryDefinition = GeoDistanceQueryDefinition(field)
  def geoBoxQuery(field: String) = GeoBoundingBoxQueryDefinition(field)
  def geoPolygonQuery(field: String) = GeoPolygonQueryDefinition(field)

  def indicesQuery(indices: String*) = new {
    def query(query: QueryDefinition): IndicesQueryDefinition = new IndicesQueryDefinition(indices, query)
  }

  def hasChildQuery(`type`: String) = new HasChildExpectsQuery(`type`)
  class HasChildExpectsQuery(`type`: String) {
    def query(q: QueryDefinition): HasChildQueryDefinition = HasChildQueryDefinition(`type`, q)
  }

  def hasParentQuery(`type`: String) = new HasParentExpectsQuery(`type`)
  class HasParentExpectsQuery(`type`: String) {
    def query(q: QueryDefinition) = new HasParentQueryDefinition(`type`, q)
  }

  def matchQuery(tuple: (String, Any)): MatchQueryDefinition = matchQuery(tuple._1, tuple._2)
  def matchQuery(field: String, value: Any): MatchQueryDefinition = new MatchQueryDefinition(field, value)

  def matchPhraseQuery(field: String, value: Any): MatchPhraseDefinition = new MatchPhraseDefinition(field, value)

  def matchPhrasePrefixQuery(field: String, value: Any) = new MatchPhrasePrefixDefinition(field, value)

  def multiMatchQuery(text: String) = new MultiMatchQueryDefinition(text)

  def matchAllQuery = new MatchAllQueryDefinition

  def missingQuery(field: String) = MissingQueryDefinition(field)

  def moreLikeThisQuery(flds: Iterable[String]): MoreLikeThisRequiresLike = new MoreLikeThisRequiresLike(flds.toSeq)
  def moreLikeThisQuery(first: String, rest: String*): MoreLikeThisRequiresLike = moreLikeThisQuery(first +: rest)

  class MoreLikeThisRequiresLike(fields: Seq[String]) {
    def like(text: String, rest: String*) = MoreLikeThisQueryDefinition(fields, text +: rest)
  }

  def nestedQuery(path: String) = new {
    def query(query: QueryDefinition) = NestedQueryDefinition(path, query)
  }

  def query(q: String): QueryStringQueryDefinition = queryStringQuery(q)
  def queryStringQuery(q: String): QueryStringQueryDefinition = new QueryStringQueryDefinition(q)

  def rangeQuery(field: String): RangeQueryDefinition = new RangeQueryDefinition(field)

  def regexQuery(tuple: (String, Any)): RegexQueryDefinition = regexQuery(tuple._1, tuple._2)
  def regexQuery(field: String, value: Any): RegexQueryDefinition = new RegexQueryDefinition(field, value)

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

  def spanMultiTermQuery(query: MultiTermQueryDefinition) = new SpanMultiTermQueryDefinition(query)

  def termQuery(tuple: (String, Any)): TermQueryDefinition = termQuery(tuple._1, tuple._2)
  def termQuery(field: String, value: Any): TermQueryDefinition = TermQueryDefinition(field, value)

  def termsQuery(field: String, values: AnyRef*) = TermsQueryDefinition(field, values.map(_.toString): _*)

  def wildcardQuery(tuple: (String, Any)): WildcardQueryDefinition = wildcardQuery(tuple._1, tuple._2)
  def wildcardQuery(field: String, value: Any): WildcardQueryDefinition = new WildcardQueryDefinition(field, value)

  def typeQuery(`type`: String) = TypeQueryDefinition(`type`)

  @deprecated("use idsQuery", "2.0.0")
  def ids(ids: Iterable[String]): IdQueryDefinition = IdQueryDefinition(ids.toSeq)
  @deprecated("use idsQuery", "2.0.0")
  def ids(ids: String*): IdQueryDefinition = IdQueryDefinition(ids.toSeq)

  def idsQuery(ids: Iterable[String]): IdQueryDefinition = IdQueryDefinition(ids.toSeq)
  def idsQuery(id: String, rest: String*): IdQueryDefinition = IdQueryDefinition(id +: rest)

  def all: MatchAllQueryDefinition = new MatchAllQueryDefinition

  def bool(block: => BoolQueryDefinition): BoolQueryDefinition = block
  def bool(mustQueries: Seq[QueryDefinition],
           shouldQueries: Seq[QueryDefinition],
           notQueries: Seq[QueryDefinition]): BoolQueryDefinition = {
    must(mustQueries).should(shouldQueries).not(notQueries)
  }
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

class FunctionScoreQueryDefinition(query: QueryDefinition)
  extends QueryDefinition
  with DefinitionAttributeBoost
  with DefinitionAttributeBoostMode
  with DefinitionAttributeMaxBoost
  with DefinitionAttributeScoreMode {

  val builder = new FunctionScoreQueryBuilder(query.builder)
  val _builder = builder

  def scorers(scorers: ScoreDefinition[_]*): FunctionScoreQueryDefinition = {
    scorers.foreach(scorer => scorer._filter match {
      case None => builder.add(scorer.builder)
      case Some(filter) => builder.add(filter.builder, scorer.builder)
    })
    this
  }
}

case class FuzzyQueryDefinition(field: String, termValue: Any)
  extends MultiTermQueryDefinition
  with DefinitionAttributePrefixLength
  with DefinitionAttributeBoost {

  val builder = QueryBuilders.fuzzyQuery(field, termValue.toString)
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

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}

case class MoreLikeThisQueryDefinition(fields: Seq[String], text: Seq[String]) extends QueryDefinition {

  val _builder = QueryBuilders.moreLikeThisQuery(fields: _*).like(text: _*)
  val builder = _builder

  def analyzer(analyser: String): this.type = {
    _builder.analyzer(analyser)
    this
  }

  def analyser(analyser: String): this.type = {
    _builder.analyzer(analyser)
    this
  }

  @deprecated("deprecated in elasticsearch", "2.0.0")
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

  def minTermFreq(freq: Int): this.type = {
    _builder.minTermFreq(freq)
    this
  }

  def stopWords(stopWords: String*): this.type = {
    _builder.stopWords(stopWords: _*)
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

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
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

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
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

case class GeoPolygonQueryDefinition(field: String)
  extends QueryDefinition {

  val builder = QueryBuilders.geoPolygonQuery(field)
  val _builder = builder

  def point(geohash: String): GeoPolygonQueryDefinition = {
    builder.addPoint(geohash)
    this
  }

  def point(lat: Double, lon: Double): GeoPolygonQueryDefinition = {
    _builder.addPoint(lat, lon)
    this
  }

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}

case class GeoDistanceQueryDefinition(field: String)
  extends QueryDefinition
  with DefinitionAttributeLat
  with DefinitionAttributeLon {

  val builder = QueryBuilders.geoDistanceQuery(field)
  val _builder = builder

  def geoDistance(geoDistance: GeoDistance): GeoDistanceQueryDefinition = {
    builder.geoDistance(geoDistance)
    this
  }

  def geohash(geohash: String): GeoDistanceQueryDefinition = {
    builder.geohash(geohash)
    this
  }

  def queryName(name: String): GeoDistanceQueryDefinition = {
    builder.queryName(name)
    this
  }

  def distance(distance: String): GeoDistanceQueryDefinition = {
    builder.distance(distance)
    this
  }

  def distance(distance: Double, unit: DistanceUnit): GeoDistanceQueryDefinition = {
    builder.distance(distance, unit)
    this
  }

  def point(lat: Double, long: Double): GeoDistanceQueryDefinition = {
    builder.point(lat, long)
    this
  }
}

case class GeoBoundingBoxQueryDefinition(field: String)
  extends QueryDefinition {

  val builder = QueryBuilders.geoBoundingBoxQuery(field)
  val _builder = builder

  private var _left: Double = _
  private var _top: Double = _
  private var _right: Double = _
  private var _bottom: Double = _

  def left(left: Double): GeoBoundingBoxQueryDefinition = {
    _left = left
    builder.topLeft(_top, _left)
    this
  }

  def top(top: Double): GeoBoundingBoxQueryDefinition = {
    _top = top
    builder.topLeft(_top, _left)
    this
  }

  def right(right: Double): GeoBoundingBoxQueryDefinition = {
    _right = right
    builder.bottomRight(_bottom, _right)
    this
  }

  def bottom(bottom: Double): GeoBoundingBoxQueryDefinition = {
    _bottom = bottom
    builder.bottomRight(_bottom, _right)
    this
  }

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}

class GeoDistanceRangeQueryDefinition(field: String)
  extends QueryDefinition
  with DefinitionAttributeTo
  with DefinitionAttributeFrom
  with DefinitionAttributeLt
  with DefinitionAttributeGt
  with DefinitionAttributeLat
  with DefinitionAttributeLon
  with DefinitionAttributePoint {

  val builder = QueryBuilders.geoDistanceRangeQuery(field)
  val _builder = builder

  def geoDistance(geoDistance: GeoDistance): GeoDistanceRangeQueryDefinition = {
    builder.geoDistance(geoDistance)
    this
  }

  def geohash(geohash: String): GeoDistanceRangeQueryDefinition = {
    builder.geohash(geohash)
    this
  }

  def gte(gte: Any): GeoDistanceRangeQueryDefinition = {
    builder.gte(gte)
    this
  }

  def lte(lte: Any): GeoDistanceRangeQueryDefinition = {
    builder.lte(lte)
    this
  }

  def includeLower(includeLower: Boolean): GeoDistanceRangeQueryDefinition = {
    builder.includeLower(includeLower)
    this
  }

  def includeUpper(includeUpper: Boolean): GeoDistanceRangeQueryDefinition = {
    builder.includeUpper(includeUpper)
    this
  }

  def queryName(name: String): GeoDistanceRangeQueryDefinition = {
    builder.queryName(name)
    this
  }
}

class GeoHashCellQuery(field: String)
  extends QueryDefinition {

  val builder = QueryBuilders.geoHashCellQuery(field)
  val _builder = builder

  def point(lat: Double, long: Double): this.type = {
    builder.point(lat, long)
    this
  }

  def geohash(geohash: String): this.type = {
    builder.geohash(geohash)
    this
  }

  def neighbours(neighbours: Boolean): this.type = {
    builder.neighbors(neighbours)
    this
  }
}

case class HasChildQueryDefinition(`type`: String, q: QueryDefinition)
  extends QueryDefinition with DefinitionAttributeBoost {

  val builder = QueryBuilders.hasChildQuery(`type`, q.builder)
  val _builder = builder

  /**
   * Defines the minimum number of children that are required to match for the parent to be considered a match.
   */
  def minChildren(min: Int): HasChildQueryDefinition = {
    builder.minChildren(min)
    this
  }

  /**
   * Configures at what cut off point only to evaluate parent documents that contain the matching parent id terms
   * instead of evaluating all parent docs.
   */
  def shortCircuitCutoff(shortCircuitCutoff: Int): HasChildQueryDefinition = {
    builder.setShortCircuitCutoff(shortCircuitCutoff)
    this
  }

  /**
   * Defines the maximum number of children that are required to match for the parent to be considered a match.
   */
  def maxChildren(max: Int): HasChildQueryDefinition = {
    builder.maxChildren(max)
    this
  }

  /**
   * Defines how the scores from the matching child documents are mapped into the parent document.
   */
  def scoreType(scoreType: String): HasChildQueryDefinition = {
    builder.scoreType(scoreType)
    this
  }

  def queryName(name: String) = {
    builder.queryName(name)
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

  def queryName(name: String) = {
    builder.queryName(name)
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

case class ConstantScoreDefinition(builder: ConstantScoreQueryBuilder) extends QueryDefinition {
  def boost(b: Double): QueryDefinition = {
    builder.boost(b.toFloat)
    this
  }
}

class CommonTermsQueryDefinition(name: String, text: String)
  extends QueryDefinition
  with DefinitionAttributeBoost
  with DefinitionAttributeCutoffFrequency {

  val builder = QueryBuilders.commonTermsQuery(name, text)
  val _builder = builder

  def queryName(queryName: String): CommonTermsQueryDefinition = {
    builder.queryName(queryName)
    this
  }

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

  def queryName(queryName: String): DisMaxDefinition = {
    builder.queryName(queryName)
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

case class ExistsQueryDefinition(field: String) extends QueryDefinition {

  val builder = QueryBuilders.existsQuery(field)

  def queryName(name: String): ExistsQueryDefinition = {
    builder.queryName(name)
    this
  }
}

@deprecated("Use boolQuery instead with a must clause for the query and a filter clause for the filter", "2.0.0")
class FilteredQueryDefinition extends QueryDefinition {

  def builder = QueryBuilders.filteredQuery(_query, _filter).boost(_boost.toFloat)

  private var _query: QueryBuilder = QueryBuilders.matchAllQuery
  private var _filter: QueryBuilder = null
  private var _boost: Double = -1d

  def boost(boost: Double): FilteredQueryDefinition = {
    _boost = boost
    this
  }

  def query(query: => QueryDefinition): FilteredQueryDefinition = {
    _query = Option(query).map(_.builder).getOrElse(_query)
    this
  }

  def filter(filter: => QueryDefinition): FilteredQueryDefinition = {
    _filter = Option(filter).map(_.builder).orNull
    this
  }

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}

case class IdQueryDefinition(ids: Seq[String],
                             types: Seq[String] = Nil,
                             boost: Option[Double] = None,
                             queryName: Option[String] = None) extends QueryDefinition {

  def builder = {
    val builder = QueryBuilders.idsQuery(types: _*).addIds(ids: _*)
    boost.foreach(b => builder.boost(b.toFloat))
    queryName.foreach(builder.queryName)
    builder
  }

  def types(types: Iterable[String]): IdQueryDefinition = copy(types = types.toSeq)
  def types(first: String, rest: String*): IdQueryDefinition = copy(types = first +: rest)

  def queryName(name: String): IdQueryDefinition = copy(queryName = Option(name))
  def boost(boost: Double): IdQueryDefinition = copy(boost = Option(boost))
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

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
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

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}

class PrefixQueryDefinition(field: String, prefix: Any)
  extends MultiTermQueryDefinition
  with DefinitionAttributeRewrite
  with DefinitionAttributeBoost {
  val builder = QueryBuilders.prefixQuery(field, prefix.toString)
  val _builder = builder

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
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

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
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

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}

class SpanMultiTermQueryDefinition(query: MultiTermQueryDefinition) extends SpanQueryDefinition {
  override val builder = QueryBuilders.spanMultiTermQueryBuilder(query.builder)
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

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}

class TermsLookupQueryDefinition(field: String)
  extends QueryDefinition {

  val builder = QueryBuilders.termsLookupQuery(field)
  val _builder = builder

  def queryName(name: String): this.type = {
    builder.queryName(name)
    this
  }

  def index(index: String): this.type = {
    builder.lookupIndex(index)
    this
  }

  def lookupType(`type`: String): this.type = {
    builder.lookupType(`type`)
    this
  }

  def id(id: String): this.type = {
    builder.lookupId(id)
    this
  }

  def path(path: String): this.type = {
    builder.lookupPath(path)
    this
  }

  def routing(routing: String): this.type = {
    builder.lookupRouting(routing)
    this
  }
}

case class TermQueryDefinition(field: String, value: Any) extends QueryDefinition {

  val builder = value match {
    case str: String => QueryBuilders.termQuery(field, str)
    case iter: Iterable[Any] => QueryBuilders.termQuery(field, iter.toArray)
    case other => QueryBuilders.termQuery(field, other)
  }

  def boost(boost: Double) = {
    builder.boost(boost.toFloat)
    this
  }

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}

case class TermsQueryDefinition(field: String, values: String*) extends QueryDefinition {

  val builder = QueryBuilders.termsQuery(field, values: _*)

  def boost(boost: Double): TermsQueryDefinition = {
    builder.boost(boost.toFloat)
    this
  }

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }

  @deprecated("deprecated in elasticsearch", "2.0.0")
  def minimumShouldMatch(min: Int): TermsQueryDefinition = minimumShouldMatch(min.toString)

  @deprecated("deprecated in elasticsearch", "2.0.0")
  def minimumShouldMatch(min: String): TermsQueryDefinition = {
    builder.minimumShouldMatch(min)
    this
  }

  @deprecated("deprecated in elasticsearch", "2.0.0")
  def disableCoord(disableCoord: Boolean): TermsQueryDefinition = {
    builder.disableCoord(disableCoord)
    this
  }
}

case class TypeQueryDefinition(`type`: String) extends QueryDefinition {
  val builder = QueryBuilders.typeQuery(`type`)
}

case class MatchAllQueryDefinition() extends QueryDefinition {

  val builder = QueryBuilders.matchAllQuery

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

  def lte(d: String): RangeQueryDefinition = {
    builder.gte(d)
    this
  }

  def timeZone(timeZone: String): RangeQueryDefinition = {
    builder.timeZone(timeZone)
    this
  }

  def gte(d: String): RangeQueryDefinition = {
    builder.gte(d)
    this
  }

  def lte(d: Double): RangeQueryDefinition = {
    builder.gte(d)
    this
  }

  def gte(d: Double): RangeQueryDefinition = {
    builder.gte(d)
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

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
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

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
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

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}

case class MissingQueryDefinition(field: String) extends QueryDefinition {

  val builder = QueryBuilders.missingQuery(field)

  def includeNull(nullValue: Boolean): MissingQueryDefinition = {
    builder.nullValue(nullValue)
    this
  }

  def existence(existence: Boolean): MissingQueryDefinition = {
    builder.existence(existence)
    this
  }

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}

class ScriptQueryDefinition(script: ScriptDefinition)
  extends QueryDefinition {

  val builder = QueryBuilders.scriptQuery(script.toJavaAPI)
  val _builder = builder

  def queryName(queryName: String): ScriptQueryDefinition = {
    builder.queryName(queryName)
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

  def queryName(queryName: String): SimpleStringQueryDefinition = {
    builder.queryName(queryName)
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

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
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

case class NestedQueryDefinition(path: String,
                                 query: QueryDefinition,
                                 boost: Option[Double] = None,
                                 inner: Option[QueryInnerHitBuilder] = None,
                                 scoreMode: Option[String] = None) extends QueryDefinition {
  require(query != null, "must specify query for nested score query")

  def builder: NestedQueryBuilder = {
    val builder = QueryBuilders.nestedQuery(path, query.builder)
    boost.foreach(b => builder.boost(b.toFloat))
    scoreMode.foreach(builder.scoreMode)
    inner.foreach(builder.innerHit)
    builder
  }

  def inner(name: String): NestedQueryDefinition = copy(inner = Option(new QueryInnerHitBuilder().setName(name)))
  def inner(inner: QueryInnerHitsDefinition): NestedQueryDefinition = copy(inner = Option(inner.builder))

  def scoreMode(scoreMode: String): NestedQueryDefinition = copy(scoreMode = Option(scoreMode))
  def boost(b: Double): NestedQueryDefinition = copy(boost = Option(b))

  def queryName(queryName: String): NestedQueryDefinition = {
    builder.queryName(queryName)
    this
  }
}

class NotQueryDefinition(filter: QueryDefinition)
  extends QueryDefinition {

  val builder = QueryBuilders.notQuery(filter.builder)
  val _builder = builder

  def queryName(queryName: String): NotQueryDefinition = {
    builder.queryName(queryName)
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
