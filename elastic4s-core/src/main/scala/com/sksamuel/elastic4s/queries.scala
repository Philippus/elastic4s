package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.DefinitionAttributes._
import com.sksamuel.elastic4s.analyzers.Analyzer
import com.sksamuel.elastic4s.query._
import org.elasticsearch.common.geo.{GeoDistance, GeoPoint}
import org.elasticsearch.common.unit.DistanceUnit.Distance
import org.elasticsearch.common.unit.{DistanceUnit, Fuzziness}
import org.elasticsearch.index.query._

import scala.language.implicitConversions
import scala.language.reflectiveCalls

/** @author Stephen Samuel */

trait QueryDsl {

  implicit def string2query(string: String): SimpleStringQueryDefinition = SimpleStringQueryDefinition(string)
  implicit def tuple2query(kv: (String, String)): TermQueryDefinition = TermQueryDefinition(kv._1, kv._2)

  def query = this

  def boostingQuery(positiveQuery: QueryDefinition,
                    negativeQuery: QueryDefinition): BoostingQueryDefinition = BoostingQueryDefinition(positiveQuery, negativeQuery)

  def commonQuery(field: String) = new CommonQueryExpectsText(field)
  def commonQuery = new CommonQueryExpectsField
  class CommonQueryExpectsField {
    def field(name: String) = new CommonQueryExpectsText(name)
  }
  class CommonQueryExpectsText(name: String) {
    def text(q: String): CommonTermsQueryDefinition = CommonTermsQueryDefinition(name, q)
    def query(q: String): CommonTermsQueryDefinition = text(q)
  }

  def constantScoreQuery(q: QueryDefinition) = ConstantScoreDefinition(QueryBuilders.constantScoreQuery(q.builder))

  def dismax = new DisMaxDefinition

  def existsQuery = ExistsQueryDefinition

  def functionScoreQuery(query: QueryDefinition): FunctionScoreQueryDefinition =
    FunctionScoreQueryDefinition().withQuery(query)

  def functionScoreQuery(query: QueryDefinition, functions: Seq[FilterFunctionDefinition]): FunctionScoreQueryDefinition =
    FunctionScoreQueryDefinition().withQuery(query).withFunctions(functions)

  def fuzzyQuery(name: String, value: Any) = FuzzyQueryDefinition(name, value)

  def geoBoxQuery(field: String) = GeoBoundingBoxQueryDefinition(field)
  def geoDistanceQuery(field: String): GeoDistanceQueryDefinition = GeoDistanceQueryDefinition(field)

  def geoHashCell(field: String, value: String): GeoHashCellQueryDefinition =
    GeoHashCellQueryDefinition(field, value)

  def geoHashCell(field: String, value: GeoPoint): GeoHashCellQueryDefinition =
    GeoHashCellQueryDefinition(field, value.geohash)

  def geoPolygonQuery(field: String) = GeoPolygonQueryDefinition(field)
  def geoDistanceRangeQuery(field: String) = GeoDistanceRangeQueryDefinition(field)

  def indicesQuery(indices: String*) = new {
    def query(query: QueryDefinition): IndicesQueryDefinition = IndicesQueryDefinition(indices, query)
  }

  def hasChildQuery(`type`: String) = new HasChildExpectsQuery(`type`)
  class HasChildExpectsQuery(`type`: String) {
    def query(q: QueryDefinition): HasChildQueryDefinition = HasChildQueryDefinition(`type`, q)
  }

  def hasParentQuery(`type`: String) = new HasParentExpectsQuery(`type`)
  class HasParentExpectsQuery(`type`: String) {
    def query(q: QueryDefinition) = HasParentQueryDefinition(`type`, q)
  }

  def matchQuery(tuple: (String, Any)): MatchQueryDefinition = matchQuery(tuple._1, tuple._2)
  def matchQuery(field: String, value: Any): MatchQueryDefinition = MatchQueryDefinition(field, value)

  def matchPhraseQuery(field: String, value: Any): MatchPhraseDefinition = MatchPhraseDefinition(field, value)

  def matchPhrasePrefixQuery(field: String, value: Any) = MatchPhrasePrefixDefinition(field, value)

  def multiMatchQuery(text: String) = MultiMatchQueryDefinition(text)

  def matchAllQuery = new MatchAllQueryDefinition

  def moreLikeThisQuery(flds: Iterable[String]): MoreLikeThisQueryDefinition = MoreLikeThisQueryDefinition(flds.toSeq)
  def moreLikeThisQuery(first: String, rest: String*): MoreLikeThisQueryDefinition = moreLikeThisQuery(first +: rest)

  def nestedQuery(path: String) = new {
    def query(query: QueryDefinition) = NestedQueryDefinition(path, query)
  }

  def query(q: String): QueryStringQueryDefinition = queryStringQuery(q)
  def queryStringQuery(q: String): QueryStringQueryDefinition = QueryStringQueryDefinition(q)

  def rangeQuery(field: String): RangeQueryDefinition = RangeQueryDefinition(field)

  def regexQuery(tuple: (String, Any)): RegexQueryDefinition = regexQuery(tuple._1, tuple._2)
  def regexQuery(field: String, value: Any): RegexQueryDefinition = RegexQueryDefinition(field, value)

  def prefixQuery(tuple: (String, Any)): PrefixQueryDefinition = prefixQuery(tuple._1, tuple._2)
  def prefixQuery(field: String, value: Any): PrefixQueryDefinition = PrefixQueryDefinition(field, value)

  def scriptQuery(script: String): ScriptQueryDefinition = ScriptQueryDefinition(script)

  def simpleStringQuery(q: String): SimpleStringQueryDefinition = SimpleStringQueryDefinition(q)
  def stringQuery(q: String): QueryStringQueryDefinition = QueryStringQueryDefinition(q)

  def spanFirstQuery = new {
    def query(spanQuery: SpanQueryDefinition) = new {
      def end(end: Int) = SpanFirstQueryDefinition(spanQuery, end)
    }
  }

  def spanNearQuery(defs: Iterable[SpanQueryDefinition], slop: Int): SpanNearQueryDefinition = SpanNearQueryDefinition(defs.toSeq, slop)

  def spanOrQuery(iterable: Iterable[SpanQueryDefinition]): SpanOrQueryDefinition = SpanOrQueryDefinition(iterable.toSeq)
  def spanOrQuery(first: SpanQueryDefinition, rest: SpanQueryDefinition*): SpanOrQueryDefinition = spanOrQuery(first +: rest)

  def spanTermQuery(field: String, value: Any): SpanTermQueryDefinition = new SpanTermQueryDefinition(field, value)

  def spanNotQuery(include: SpanQueryDefinition, exclude: SpanQueryDefinition): SpanNotQueryDefinition =
    SpanNotQueryDefinition(include, exclude)

  def spanMultiTermQuery(query: MultiTermQueryDefinition) = SpanMultiTermQueryDefinition(query)

  def termQuery(tuple: (String, Any)): TermQueryDefinition = termQuery(tuple._1, tuple._2)
  def termQuery(field: String, value: Any): TermQueryDefinition = TermQueryDefinition(field, value)

  def termsQuery(field: String, values: AnyRef*): TermsQueryDefinition = {
    TermsQueryDefinition(field, values.map(_.toString))
  }

  def termsQuery(field: String, values: Int*): IntTermsQueryDefinition = {
    IntTermsQueryDefinition(field, values)
  }

  def termsQuery(field: String, values: Long*): LongTermsQueryDefinition = {
    LongTermsQueryDefinition(field, values)
  }

  def termsQuery(field: String, values: Float*): FloatTermsQueryDefinition = {
    FloatTermsQueryDefinition(field, values)
  }

  def termsQuery(field: String, values: Double*): DoubleTermsQueryDefinition = {
    DoubleTermsQueryDefinition(field, values)
  }

  def wildcardQuery(tuple: (String, Any)): WildcardQueryDefinition = wildcardQuery(tuple._1, tuple._2)
  def wildcardQuery(field: String, value: Any): WildcardQueryDefinition = WildcardQueryDefinition(field, value)

  def typeQuery(`type`: String) = TypeQueryDefinition(`type`)

  @deprecated("use idsQuery", "2.0.0")
  def ids(ids: Iterable[String]): IdQueryDefinition = IdQueryDefinition(ids.toSeq)
  @deprecated("use idsQuery", "2.0.0")
  def ids(ids: String*): IdQueryDefinition = IdQueryDefinition(ids.toSeq)

  def idsQuery(ids: Iterable[String]): IdQueryDefinition = IdQueryDefinition(ids.toSeq)
  def idsQuery(id: String, rest: String*): IdQueryDefinition = IdQueryDefinition(id +: rest)

  def all: MatchAllQueryDefinition = new MatchAllQueryDefinition

  // -- bool query dsl ---
  def bool(block: => BoolQueryDefinition): BoolQueryDefinition = block
  def bool(mustQueries: Seq[QueryDefinition],
           shouldQueries: Seq[QueryDefinition],
           notQueries: Seq[QueryDefinition]): BoolQueryDefinition = {
    must(mustQueries).should(shouldQueries).not(notQueries)
  }
  def must(queries: QueryDefinition*): BoolQueryDefinition = new BoolQueryDefinition().must(queries: _*)
  def must(queries: Iterable[QueryDefinition]): BoolQueryDefinition = new BoolQueryDefinition().must(queries)
  def filter(first: QueryDefinition, rest: QueryDefinition*): BoolQueryDefinition = filter(first +: rest)
  def filter(queries: Iterable[QueryDefinition]): BoolQueryDefinition = new BoolQueryDefinition().filter(queries)
  def should(queries: QueryDefinition*): BoolQueryDefinition = new BoolQueryDefinition().should(queries: _*)
  def should(queries: Iterable[QueryDefinition]): BoolQueryDefinition = new BoolQueryDefinition().should(queries)
  def not(queries: QueryDefinition*): BoolQueryDefinition = new BoolQueryDefinition().not(queries: _*)
  def not(queries: Iterable[QueryDefinition]): BoolQueryDefinition = new BoolQueryDefinition().not(queries)
}


trait QueryDefinition {
  def builder: org.elasticsearch.index.query.QueryBuilder
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

case class Item(index: String, `type`: String, id: String)

case class MoreLikeThisQueryDefinition(fields: Seq[String]) extends QueryDefinition {

  val _builder = QueryBuilders.moreLikeThisQuery(fields: _*)
  val builder = _builder

  def like(first: String, rest: String*): this.type = like(first +: rest)
  def like(likes: Iterable[String]): this.type = {
    _builder.like(likes.toSeq: _*)
    this
  }

  def like(item: Item, rest: Item*): this.type = like(item +: rest)
  def like(items: Seq[Item]): this.type = {
    builder.like(items.map(item => new MoreLikeThisQueryBuilder.Item(item.index, item.`type`, item.id)): _ *)
    this
  }

  def analyzer(analyser: String): this.type = {
    _builder.analyzer(analyser)
    this
  }

  @deprecated("Use unlike", "2.1.0")
  def ignoreLike(first: String, rest: String*): this.type = unlike(first +: rest)
  @deprecated("Use unlike", "2.1.0")
  def ignoreLike(likes: Iterable[String]): this.type = {
    _builder.unlike(likes.toSeq: _*)
    this
  }

  def unlike(first: String, rest: String*): this.type = unlike(first +: rest)
  def unlike(likes: Iterable[String]): this.type = {
    _builder.unlike(likes.toSeq: _*)
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

  def distance(distance: Distance): GeoDistanceQueryDefinition = {
    builder.distance(distance.value, distance.unit)
    this
  }

  def point(lat: Double, long: Double): GeoDistanceQueryDefinition = {
    builder.point(lat, long)
    this
  }
}


case class GeoDistanceRangeQueryDefinition(field: String)
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
  def scoreMode(scoreMode: String): HasChildQueryDefinition = {
    builder.scoreMode(scoreMode)
    this
  }

  /**
    * Defines how the scores from the matching child documents are mapped into the parent document.
    */
  @deprecated("use scoreMode", "2.1.0")
  def scoreType(scoreType: String): HasChildQueryDefinition = {
    builder.scoreType(scoreType)
    this
  }

  def queryName(name: String) = {
    builder.queryName(name)
    this
  }
}

case class HasParentQueryDefinition(`type`: String, q: QueryDefinition)
  extends QueryDefinition with DefinitionAttributeBoost {

  val builder = QueryBuilders.hasParentQuery(`type`, q.builder)
  val _builder = builder

  def scoreMode(scoreMode: String): HasParentQueryDefinition = {
    builder.scoreMode(scoreMode)
    this
  }

  @deprecated("use scoreMode", "2.1.0")
  def scoreType(scoreType: String): HasParentQueryDefinition = {
    builder.scoreType(scoreType)
    this
  }

  def queryName(name: String) = {
    builder.queryName(name)
    this
  }
}

case class IndicesQueryDefinition(indices: Iterable[String], query: QueryDefinition) extends QueryDefinition {

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

case class WildcardQueryDefinition(field: String, query: Any)
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

case class PrefixQueryDefinition(field: String, prefix: Any)
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

case class RegexQueryDefinition(field: String, regex: Any)
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

case class SpanFirstQueryDefinition(query: SpanQueryDefinition, end: Int) extends QueryDefinition {
  val builder = QueryBuilders.spanFirstQuery(query.builder, end)
}

case class SpanNotQueryDefinition(include: SpanQueryDefinition,
                                  exclude: SpanQueryDefinition) extends QueryDefinition {

  val builder = QueryBuilders.spanNotQuery(include.builder, exclude.builder)

  def boost(boost: Double): this.type = {
    builder.boost(boost.toFloat)
    this
  }

  def dist(dist: Int): this.type = {
    builder.dist(dist)
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

case class SpanMultiTermQueryDefinition(query: MultiTermQueryDefinition) extends SpanQueryDefinition {
  override val builder = QueryBuilders.spanMultiTermQueryBuilder(query.builder)
}


case class TermsLookupQueryDefinition(field: String)
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

trait GenericTermsQueryDefinition extends QueryDefinition {
  def builder: TermsQueryBuilder

  def boost(boost: Double): this.type = {
    builder.boost(boost.toFloat)
    this
  }

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}

case class TermsQueryDefinition(field: String, values: Seq[String]) extends GenericTermsQueryDefinition {

  val builder: TermsQueryBuilder = QueryBuilders.termsQuery(field, values: _*)

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

case class IntTermsQueryDefinition(field: String, values: Seq[Int]) extends GenericTermsQueryDefinition {
  val builder: TermsQueryBuilder = QueryBuilders.termsQuery(field, values: _*)
}

case class LongTermsQueryDefinition(field: String, values: Seq[Long]) extends GenericTermsQueryDefinition {
  val builder: TermsQueryBuilder = QueryBuilders.termsQuery(field, values: _*)
}

case class FloatTermsQueryDefinition(field: String, values: Seq[Float]) extends GenericTermsQueryDefinition {
  val builder: TermsQueryBuilder = QueryBuilders.termsQuery(field, values: _*)
}

case class TypeQueryDefinition(`type`: String) extends QueryDefinition {
  val builder = QueryBuilders.typeQuery(`type`)
}


case class ScriptQueryDefinition(script: ScriptDefinition)
  extends QueryDefinition {

  val builder = QueryBuilders.scriptQuery(script.toJavaAPI)
  val _builder = builder

  def queryName(queryName: String): ScriptQueryDefinition = {
    builder.queryName(queryName)
    this
  }
}

case class SimpleStringQueryDefinition(query: String) extends QueryDefinition {

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

case class QueryStringQueryDefinition(query: String)
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
                                 queryName: Option[String] = None,
                                 scoreMode: Option[String] = None) extends QueryDefinition {
  require(query != null, "must specify query for nested score query")

  def builder: NestedQueryBuilder = {
    val builder = QueryBuilders.nestedQuery(path, query.builder)
    boost.foreach(b => builder.boost(b.toFloat))
    scoreMode.foreach(builder.scoreMode)
    inner.foreach(builder.innerHit)
    queryName.foreach(builder.queryName)
    builder
  }

  def inner(name: String): NestedQueryDefinition = copy(inner = Option(new QueryInnerHitBuilder().setName(name)))
  def inner(inner: QueryInnerHitsDefinition): NestedQueryDefinition = copy(inner = Option(inner.builder))

  def scoreMode(scoreMode: String): NestedQueryDefinition = copy(scoreMode = Option(scoreMode))
  def boost(b: Double): NestedQueryDefinition = copy(boost = Option(b))
  def queryName(queryName: String): NestedQueryDefinition = copy(queryName = Option(queryName))
}

case class QueryInnerHitsDefinition(private[elastic4s] val name: String) {

  private[elastic4s] val builder = new QueryInnerHitBuilder().setName(name)
  private var includes: Array[String] = Array.empty
  private var excludes: Array[String] = Array.empty

  def from(f: Int): this.type = {
    builder.setFrom(f)
    this
  }

  def size(s: Int): this.type = {
    builder.setSize(s)
    this
  }

  def highlighting(highlights: HighlightDefinition*): this.type = {
    highlights.foreach(highlight => builder.addHighlightedField(highlight.builder))
    this
  }

  def fetchSource(fetch: Boolean): this.type = {
    builder.setFetchSource(fetch)
    this
  }

  def sourceInclude(includes: String*): this.type = {
    this.includes = includes.toArray
    builder.setFetchSource(this.includes, excludes)
    this
  }

  def sourceExclude(excludes: String*): this.type = {
    this.excludes = excludes.toArray
    builder.setFetchSource(includes, this.excludes)
    this
  }
}

case class InnerHitDefinition(private[elastic4s] val name: String) {

  private[elastic4s] val inner = new InnerHit

  def path(p: String): this.type = {
    inner.setPath(p)
    this
  }

  def `type`(t: String): this.type = {
    inner.setType(t)
    this
  }

  def highlighting(highlights: HighlightDefinition*): this.type = {
    highlights.foreach(highlight => inner.addHighlightedField(highlight.builder))
    this
  }
}
