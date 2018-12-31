package com.sksamuel.elastic4s.requests.searches

import com.sksamuel.elastic4s.Indexable
import com.sksamuel.elastic4s.requests.common.{DistanceUnit, DocumentRef}
import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.elastic4s.requests.searches.queries._
import com.sksamuel.elastic4s.requests.searches.queries.funcscorer.FunctionScoreQuery
import com.sksamuel.elastic4s.requests.searches.queries.geo._
import com.sksamuel.elastic4s.requests.searches.queries.matches._
import com.sksamuel.elastic4s.requests.searches.queries.span._
import com.sksamuel.elastic4s.requests.searches.queries.term._

import scala.language.{implicitConversions, reflectiveCalls}

trait QueryApi {

  implicit def string2query(string: String): SimpleStringQuery = SimpleStringQuery(string)
  implicit def tuple2query(kv: (String, String)): TermQuery    = TermQuery(kv._1, kv._2)

  def boostingQuery(positiveQuery: Query, negativeQuery: Query): BoostingQuery =
    BoostingQuery(positiveQuery, negativeQuery)

  @deprecated("use commonTermsQuery", "6.1.2")
  def commonQuery(field: String)      = new CommonQueryExpectsText(field)
  def commonTermsQuery(field: String) = new CommonQueryExpectsText(field)

  class CommonQueryExpectsText(name: String) {
    def text(q: String): CommonTermsQuery  = CommonTermsQuery(name, q)
    def query(q: String): CommonTermsQuery = text(q)
  }

  @deprecated("use commonTermsQuery", "6.1.2")
  def commonQuery(field: String, text: String)      = CommonTermsQuery(field, text)
  def commonTermsQuery(field: String, text: String) = CommonTermsQuery(field, text)

  def constantScoreQuery(query: Query): ConstantScore = ConstantScore(query)

  def dismax(first: Query, rest: Query*): DisMaxQuery = dismax(first +: rest)
  def dismax(queries: Iterable[Query]): DisMaxQuery   = DisMaxQuery(queries.toSeq)

  def existsQuery(field: String) = ExistsQuery(field)

  def fuzzyQuery(field: String, value: String): FuzzyQuery = FuzzyQuery(field, value)

  def functionScoreQuery(): FunctionScoreQuery             = FunctionScoreQuery()
  def functionScoreQuery(query: Query): FunctionScoreQuery = functionScoreQuery().query(query)

  def geoBoxQuery(field: String) = GeoBoundingBoxQuery(field)
  def geoBoxQuery(field: String, topleft: String, bottomright: String): GeoBoundingBoxQuery =
    GeoBoundingBoxQuery(field).withGeohash(topleft, bottomright)

  def geoDistanceQuery(field: String): GeoDistanceExpectsPoint           = new GeoDistanceExpectsPoint(field)
  def geoDistanceQuery(field: String, geohash: String): GeoDistanceQuery = GeoDistanceQuery(field).geohash(geohash)
  def geoDistanceQuery(field: String, lat: Double, long: Double): GeoDistanceQuery =
    GeoDistanceQuery(field).point(lat, long)

  @deprecated("use geoDistanceQuery(field, hash) or geoDistanceQuery(field, lat, long)", "6.1.2")
  class GeoDistanceExpectsPoint(field: String) {
    def geohash(geohash: String): GeoDistanceQuery         = GeoDistanceQuery(field).geohash(geohash)
    def point(lat: Double, long: Double): GeoDistanceQuery = GeoDistanceQuery(field).point(lat, long)
  }

  class GeoDistanceExpectsDistance(gdef: GeoDistanceQuery) {
    def distance(distance: String): GeoDistanceQuery                     = gdef.distance(distance)
    def distance(distance: Double, unit: DistanceUnit): GeoDistanceQuery = gdef.distance(distance, unit)
  }

  def geoPolygonQuery(field: String) = new GeoPolygonExpectsPoints(field)
  class GeoPolygonExpectsPoints(field: String) {
    def points(first: GeoPoint, rest: GeoPoint*): GeoPolygonQuery = points(first +: rest)
    def points(points: Iterable[GeoPoint]): GeoPolygonQuery       = geoPolygonQuery(field, points)
  }

  def geoPolygonQuery(field: String, first: GeoPoint, rest: GeoPoint*): GeoPolygonQuery =
    geoPolygonQuery(field, first +: rest)

  def geoPolygonQuery(field: String, points: Iterable[GeoPoint]): GeoPolygonQuery =
    GeoPolygonQuery(field, points.toSeq)

  def geoShapeQuery(field: String, shape: Shape): GeoShapeQuery = GeoShapeQuery(field, shape)

  @deprecated("use hasChildQuery(`type`: String, query: Query, score: Boolean)", "6.3.0")
  def hasChildQuery(`type`: String): HasChildQueryExpectsQuery = new HasChildQueryExpectsQuery(`type`)

  def hasChildQuery(childType: String, query: Query, scoreMode: ScoreMode = ScoreMode.None): HasChildQuery =
    HasChildQuery(childType, query, scoreMode)

  class HasChildQueryExpectsQuery(`type`: String) {
    def query(q: Query): ExpectsScoreMode  = new ExpectsScoreMode(q)
    def query(q: String): ExpectsScoreMode = new ExpectsScoreMode(q)
    class ExpectsScoreMode(q: Query) {
      def scoreMode(mode: String): HasChildQuery         = scoreMode(ScoreMode.valueOf(mode))
      def scoreMode(scoreMode: ScoreMode): HasChildQuery = hasChildQuery(`type`, q, scoreMode)
    }
  }

  @deprecated("use hasParentQuery(`type`: String, query: Query, score: Boolean)", "6.3.0")
  def hasParentQuery(`type`: String) = new HasParentQueryExpectsQuery(`type`)

  def hasParentQuery(parentType: String, query: Query, score: Boolean) =
    HasParentQuery(parentType, query, score)

  class HasParentQueryExpectsQuery(`type`: String) {
    def query(q: Query) = new ExpectsScoreMode(q)
    class ExpectsScoreMode(q: Query) {
      def scoreMode(scoreMode: Boolean): HasParentQuery = hasParentQuery(`type`, q, scoreMode)
    }
  }

  def innerHits(name: String): com.sksamuel.elastic4s.requests.searches.queries.InnerHit = new com.sksamuel.elastic4s.requests.searches.queries.InnerHit(name)

  @deprecated("use matchQuery(field, value) instead of the tupled version", "5.2.0")
  def matchQuery(tuple: (String, Any)): MatchQuery      = MatchQuery(tuple._1, tuple._2)
  def matchQuery(field: String, value: Any): MatchQuery = MatchQuery(field, value)

  def matchPhraseQuery(field: String, value: Any): MatchPhrase = MatchPhrase(field, value)

  def matchPhrasePrefixQuery(field: String, value: Any) = MatchPhrasePrefix(field, value)

  def multiMatchQuery(text: String) = MultiMatchQuery(text)

  def matchNoneQuery() = MatchNoneQuery()
  def matchAllQuery()  = MatchAllQuery()

  def moreLikeThisQuery(field: String, fields: String*): MoreLikeThisExpectsLikes = moreLikeThisQuery(field +: fields)
  def moreLikeThisQuery(fields: Iterable[String]): MoreLikeThisExpectsLikes = new MoreLikeThisExpectsLikes(fields.toSeq)
  def moreLikeThisQuery(): MoreLikeThisExpectsLikes = new MoreLikeThisExpectsLikes(Nil)

  class MoreLikeThisExpectsLikes(fields: Seq[String]) {

    def likeTexts(first: String, rest: String*): MoreLikeThisQuery = likeTexts(first +: rest)

    def likeTexts(texts: Iterable[String]): MoreLikeThisQuery =
      MoreLikeThisQuery(fields, texts.toSeq)

    def likeItems(first: MoreLikeThisItem, rest: MoreLikeThisItem*): MoreLikeThisQuery =
      likeItems(first +: rest)

    def likeItems(items: Iterable[MoreLikeThisItem]): MoreLikeThisQuery =
      MoreLikeThisQuery(fields).copy(likeDocs = items.toSeq)

    def likeDocs(first: DocumentRef, rest: DocumentRef*): MoreLikeThisQuery = likeDocs(first +: rest)

    def likeDocs(docs: Iterable[DocumentRef]): MoreLikeThisQuery =
      likeItems(docs.map { d =>
        MoreLikeThisItem(d)
      })

    def artificialDocs(first: ArtificialDocument, rest: ArtificialDocument*): MoreLikeThisQuery =
      artificialDocs(first +: rest)

    def artificialDocs(docs: Iterable[ArtificialDocument]): MoreLikeThisQuery =
      MoreLikeThisQuery(fields).copy(artificialDocs = docs.toSeq)
  }

  def nestedQuery(path: String) = new NestedQueryExpectsQuery(path)
  class NestedQueryExpectsQuery(path: String) {
    class NestedQueryExpectsScoreMode(query: Query)
    def query(query: Query): NestedQuery = nestedQuery(path, query)
  }
  def nestedQuery(path: String, query: Query): NestedQuery = NestedQuery(path, query)

  def query(queryString: String): QueryStringQuery            = queryStringQuery(queryString)
  def queryStringQuery(queryString: String): QueryStringQuery = QueryStringQuery(queryString)

  def percolateQuery(`type`: String, field: String = "query") = new PercolateExpectsUsing(`type`, field)
  class PercolateExpectsUsing(`type`: String, field: String) {
    def usingId(index: String, `type`: String, id: Any): PercolateQuery =
      usingId(DocumentRef(index, `type`, id.toString))

    def usingId(ref: DocumentRef): PercolateQuery =
      PercolateQuery(field, `type`, ref = Some(ref))

    def usingSource(json: String): PercolateQuery =
      PercolateQuery(field, `type`, source = Some(json))

    def usingSource[T](t: T)(implicit indexable: Indexable[T]): PercolateQuery =
      PercolateQuery(field, `type`, source = Some(indexable.json(t)))
  }

  def rangeQuery(field: String): RangeQuery = RangeQuery(field)

  def rawQuery(json: String): RawQuery = RawQuery(json)

  @deprecated("use the non-tupled version regexQuery(field,value)", "6.1.2")
  def regexQuery(tuple: (String, String)): RegexQuery      = regexQuery(tuple._1, tuple._2)
  def regexQuery(field: String, value: String): RegexQuery = RegexQuery(field, value)

  @deprecated("use the non-tupled version prefixQuery(field,value)", "6.1.2")
  def prefixQuery(tuple: (String, Any)): PrefixQuery      = prefixQuery(tuple._1, tuple._2)
  def prefixQuery(field: String, value: Any): PrefixQuery = PrefixQuery(field, value)

  def scriptQuery(script: Script): ScriptQuery = ScriptQuery(script)
  def scriptQuery(script: String): ScriptQuery = ScriptQuery(script)

  def simpleStringQuery(q: String): SimpleStringQuery = SimpleStringQuery(q)
  def stringQuery(q: String): QueryStringQuery        = QueryStringQuery(q)

  def spanFirstQuery(query: SpanQuery) = new SpanFirstExpectsEnd(query)
  class SpanFirstExpectsEnd(query: SpanQuery) {
    def end(end: Int) = SpanFirstQuery(query, end)
  }

  def spanNearQuery(defs: Iterable[SpanQuery], slop: Int): SpanNearQuery =
    SpanNearQuery(defs.toSeq, slop)

  def spanOrQuery(iterable: Iterable[SpanQuery]): SpanOrQuery =
    SpanOrQuery(iterable.toSeq)
  def spanOrQuery(first: SpanQuery, rest: SpanQuery*): SpanOrQuery =
    spanOrQuery(first +: rest)

  def spanContainingQuery(big: SpanQuery, little: SpanQuery): SpanContainingQuery =
    SpanContainingQuery(big, little)

  def spanWithinQuery(big: SpanQuery, little: SpanQuery): SpanWithinQuery =
    SpanWithinQuery(big, little)

  def spanTermQuery(field: String, value: Any): SpanTermQuery = SpanTermQuery(field, value)

  def spanNotQuery(include: SpanQuery, exclude: SpanQuery): SpanNotQuery =
    SpanNotQuery(include, exclude)

  def spanMultiTermQuery(query: MultiTermQuery) = SpanMultiTermQuery(query)

  @deprecated("use the non-tupled version termQuery(field,value)", "6.1.2")
  def termQuery(tuple: (String, Any)): TermQuery      = termQuery(tuple._1, tuple._2)
  def termQuery(field: String, value: Any): TermQuery = TermQuery(field, value)

  def termsQuery[T](field: String, first: T, rest: T*): TermsQuery[T] =
    termsQuery(field, first +: rest)

  def termsQuery[T](field: String, values: Iterable[T]) =
    TermsQuery(field, values)

  def termsLookupQuery(field: String, path: String, ref: DocumentRef) =
    TermsLookupQuery(field, TermsLookup(ref, path))

  // Either minimumShouldMatchField or minimumShouldMatchScript should be specified, that's why they appear as mandatory parameters
  def termsSetQuery(field: String,
                    terms: Set[String],
                    minimumShouldMatchField: String): com.sksamuel.elastic4s.requests.searches.queries.term.TermsSetQuery =
    TermsSetQuery(field, terms, minimumShouldMatchField = Some(minimumShouldMatchField))

  def termsSetQuery(field: String,
                    terms: Set[String],
                    minimumShouldMatchScript: Script): com.sksamuel.elastic4s.requests.searches.queries.term.TermsSetQuery =
    TermsSetQuery(field, terms, minimumShouldMatchScript = Some(minimumShouldMatchScript))

  @deprecated("use the non-tupled version wildcardQuery(field,value)", "6.1.2")
  def wildcardQuery(tuple: (String, Any)): WildcardQuery      = wildcardQuery(tuple._1, tuple._2)
  def wildcardQuery(field: String, value: Any): WildcardQuery = WildcardQuery(field, value)

  def typeQuery(`type`: String) = TypeQuery(`type`)

  def idsQuery(ids: Iterable[Any]): IdQuery  = IdQuery(ids.toSeq)
  def idsQuery(id: Any, rest: Any*): IdQuery = IdQuery(id +: rest)

  def bool(mustQueries: Seq[Query], shouldQueries: Seq[Query], notQueries: Seq[Query]): BoolQuery =
    must(mustQueries).should(shouldQueries).not(notQueries)

  // convenience to make an emtpy bool which can be appended to
  def boolQuery(): BoolQuery = BoolQuery()

  // short cut for a boolean query with musts
  def must(first: Query, rest: Query*): BoolQuery = must(first +: rest)
  def must(queries: Iterable[Query]): BoolQuery   = BoolQuery().must(queries)

  // short cut for a boolean query with shoulds
  def should(queries: Query*): BoolQuery          = BoolQuery().should(queries: _*)
  def should(queries: Iterable[Query]): BoolQuery = BoolQuery().should(queries)

  // short cut for a boolean query with nots
  def not(queries: Query*): BoolQuery          = BoolQuery().not(queries: _*)
  def not(queries: Iterable[Query]): BoolQuery = BoolQuery().not(queries)
}
