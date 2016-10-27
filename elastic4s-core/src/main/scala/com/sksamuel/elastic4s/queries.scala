package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.DefinitionAttributes._
import com.sksamuel.elastic4s.query._
import org.elasticsearch.common.geo.GeoPoint
import org.elasticsearch.index.query._

import scala.language.{implicitConversions, reflectiveCalls}

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

  def dismax(queries: Iterable[QueryDefinition]): DisMaxDefinition = new DisMaxDefinition(queries.toSeq)
  def dismax(first: QueryDefinition, rest: QueryDefinition*): DisMaxDefinition = dismax(first +: rest)

  def existsQuery = ExistsQueryDefinition

  def functionScoreQuery(query: QueryDefinition): FunctionScoreQueryDefinition =
    FunctionScoreQueryDefinition().withQuery(query)

  def functionScoreQuery(query: QueryDefinition, functions: Seq[FilterFunctionDefinition]): FunctionScoreQueryDefinition =
    FunctionScoreQueryDefinition().withQuery(query).withFunctions(functions)

  def geoBoxQuery(field: String) = GeoBoundingBoxQueryDefinition(field)

  def geoDistanceQuery(field: String): GeoDistanceQueryDefinition = GeoDistanceQueryDefinition(field)

  def geoHashCell(field: String, value: String): GeoHashCellQueryDefinition =
    GeoHashCellQueryDefinition(field, value)

  def geoHashCell(field: String, value: GeoPoint): GeoHashCellQueryDefinition =
    GeoHashCellQueryDefinition(field, value.geohash)

  def geoPolygonQuery(field: String, first: GeoPoint, rest: GeoPoint*): GeoPolygonQueryDefinition = geoPolygonQuery(field, first +: rest)
  def geoPolygonQuery(field: String, points: Seq[GeoPoint]): GeoPolygonQueryDefinition = GeoPolygonQueryDefinition(field, points)

  def geoDistanceRangeQuery(field: String, geoPoint: GeoPoint) = GeoDistanceRangeQueryDefinition(field, geoPoint)

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
