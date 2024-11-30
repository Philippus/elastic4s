package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.Indexable
import com.sksamuel.elastic4s.requests.common.{DistanceUnit, DocumentRef}
import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.elastic4s.requests.searches.queries.compound.BoolQuery
import com.sksamuel.elastic4s.requests.searches.queries.funcscorer.FunctionScoreQuery
import com.sksamuel.elastic4s.requests.searches.queries.geo.{
  GeoBoundingBoxQuery,
  GeoDistanceQuery,
  GeoPolygonQuery,
  GeoShapeQuery,
  Shape
}
import com.sksamuel.elastic4s.requests.searches.queries.matches.{
  MatchAllQuery,
  MatchBoolPrefixQuery,
  MatchNoneQuery,
  MatchPhrasePrefixQuery,
  MatchPhraseQuery,
  MatchQuery,
  MultiMatchQuery
}
import com.sksamuel.elastic4s.requests.searches.queries.{
  ArtificialDocument,
  BoostingQuery,
  CombinedFieldsQuery,
  ConstantScore,
  DisMaxQuery,
  DistanceFeatureQuery,
  ExistsQuery,
  FuzzyQuery,
  HasChildQuery,
  HasParentQuery,
  IdQuery,
  IntervalsQuery,
  IntervalsRule,
  MoreLikeThisItem,
  MoreLikeThisQuery,
  MultiTermQuery,
  NestedQuery,
  PercolateQuery,
  PinnedQuery,
  PrefixQuery,
  Query,
  QueryStringQuery,
  RangeQuery,
  RankFeatureQuery,
  RawQuery,
  RegexQuery,
  ScriptQuery,
  ScriptScoreQuery,
  SemanticQuery,
  SimpleStringQuery,
  SparseVectorQuery
}
import com.sksamuel.elastic4s.requests.searches.span.{
  SpanContainingQuery,
  SpanFieldMaskingQuery,
  SpanFirstQuery,
  SpanMultiTermQuery,
  SpanNearQuery,
  SpanNotQuery,
  SpanOrQuery,
  SpanQuery,
  SpanTermQuery,
  SpanWithinQuery
}
import com.sksamuel.elastic4s.requests.searches.term.{
  TermQuery,
  TermsLookupQuery,
  TermsQuery,
  TermsSetQuery,
  WildcardQuery
}
import com.sksamuel.elastic4s.requests.searches.{GeoPoint, ScoreMode, TermsLookup, span, term}

trait QueryApi {

  implicit def string2query(string: String): SimpleStringQuery = SimpleStringQuery(string)
  implicit def tuple2query(kv: (String, String)): TermQuery    = TermQuery(kv._1, kv._2)

  def boostingQuery(positiveQuery: Query, negativeQuery: Query): BoostingQuery =
    BoostingQuery(positiveQuery, negativeQuery)

  def combinedFieldsQuery(query: String, fields: Seq[String]): CombinedFieldsQuery =
    CombinedFieldsQuery(query, fields.map(_ -> None))

  def constantScoreQuery(query: Query): ConstantScore = ConstantScore(query)

  def dismax(first: Query, rest: Query*): DisMaxQuery = dismax(first +: rest)
  def dismax(queries: Iterable[Query]): DisMaxQuery   = DisMaxQuery(queries.toSeq)

  def distanceFeatureQuery(field: String, origin: String, pivot: String) = DistanceFeatureQuery(field, origin, pivot)

  def existsQuery(field: String): ExistsQuery = ExistsQuery(field)

  def fuzzyQuery(field: String, value: String): FuzzyQuery = FuzzyQuery(field, value)

  def functionScoreQuery(): FunctionScoreQuery             = FunctionScoreQuery()
  def functionScoreQuery(query: Query): FunctionScoreQuery = functionScoreQuery().query(query)

  def geoBoxQuery(field: String): GeoBoundingBoxQuery                                       = GeoBoundingBoxQuery(field)
  def geoBoxQuery(field: String, topleft: String, bottomright: String): GeoBoundingBoxQuery =
    GeoBoundingBoxQuery(field).withGeohash(topleft, bottomright)

  @deprecated("use geoDistanceQuery(field, hash) or geoDistanceQuery(field, lat, long)", "7.2.0")
  def geoDistanceQuery(field: String): GeoDistanceExpectsPoint                     = new GeoDistanceExpectsPoint(field)
  def geoDistanceQuery(field: String, geohash: String): GeoDistanceQuery           = GeoDistanceQuery(field).geohash(geohash)
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

  def hasParentQuery(parentType: String, query: Query, score: Boolean): HasParentQuery =
    HasParentQuery(parentType, query, score)

  class HasParentQueryExpectsQuery(`type`: String) {
    def query(q: Query) = new ExpectsScoreMode(q)
    class ExpectsScoreMode(q: Query) {
      def scoreMode(scoreMode: Boolean): HasParentQuery = hasParentQuery(`type`, q, scoreMode)
    }
  }

  def innerHits(name: String): com.sksamuel.elastic4s.requests.searches.queries.InnerHit =
    com.sksamuel.elastic4s.requests.searches.queries.InnerHit(name)

  def intervalsQuery(field: String, rule: IntervalsRule): IntervalsQuery = IntervalsQuery(field, rule)

  def matchQuery(field: String, value: Any): MatchQuery = MatchQuery(field, value)

  def matchPhraseQuery(field: String, value: Any): MatchPhraseQuery = MatchPhraseQuery(field, value)

  def matchPhrasePrefixQuery(field: String, value: Any): MatchPhrasePrefixQuery = MatchPhrasePrefixQuery(field, value)
  def matchBoolPrefixQuery(field: String, value: Any): MatchBoolPrefixQuery     = MatchBoolPrefixQuery(field, value)

  def multiMatchQuery(text: String): MultiMatchQuery = MultiMatchQuery(text)

  def matchNoneQuery(): MatchNoneQuery = MatchNoneQuery()
  def matchAllQuery(): MatchAllQuery   = MatchAllQuery()

  def moreLikeThisQuery(field: String, fields: String*): MoreLikeThisExpectsLikes = moreLikeThisQuery(field +: fields)
  def moreLikeThisQuery(fields: Iterable[String]): MoreLikeThisExpectsLikes       = new MoreLikeThisExpectsLikes(fields.toSeq)
  def moreLikeThisQuery(): MoreLikeThisExpectsLikes                               = new MoreLikeThisExpectsLikes(Nil)

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

  @deprecated("use nestedQuery(path, query)", "7.7.0")
  def nestedQuery(path: String) = new NestedQueryExpectsQuery(path)
  class NestedQueryExpectsQuery(path: String) {
    def query(query: Query): NestedQuery = nestedQuery(path, query)
  }
  def nestedQuery(path: String, query: Query): NestedQuery = NestedQuery(path, query)

  def query(queryString: String): QueryStringQuery            = queryStringQuery(queryString)
  def queryStringQuery(queryString: String): QueryStringQuery = QueryStringQuery(queryString)

  def percolateQuery(`type`: String, field: String = "query") = new PercolateExpectsUsing(`type`, field)

  @deprecated("types are going away", "7.2.0")
  class PercolateExpectsUsing(`type`: String, field: String) {

    @deprecated("types are going away", "7.7.0")
    def usingId(index: String, `type`: String, id: Any): PercolateQuery =
      usingId(DocumentRef(index, `type`, id.toString))

    def usingId(ref: DocumentRef): PercolateQuery =
      PercolateQuery(field, `type`, ref = Some(ref))

    def usingSource(json: String): PercolateQuery =
      PercolateQuery(field, `type`, source = Some(json))

    def usingSource[T](t: T)(implicit indexable: Indexable[T]): PercolateQuery =
      PercolateQuery(field, `type`, source = Some(indexable.json(t)))
  }

  def pinnedQuery(ids: List[String], organic: Query): PinnedQuery = PinnedQuery(ids, organic)

  def rangeQuery(field: String): RangeQuery = RangeQuery(field)

  def rankFeatureQuery(field: String): RankFeatureQuery = RankFeatureQuery(field)

  def rawQuery(json: String): RawQuery = RawQuery(json)

  def regexQuery(field: String, value: String): RegexQuery = RegexQuery(field, value)

  def prefixQuery(field: String, value: Any): PrefixQuery = PrefixQuery(field, value)

  def scriptQuery(script: Script): ScriptQuery = ScriptQuery(script)
  def scriptQuery(script: String): ScriptQuery = ScriptQuery(script)

  def scriptScoreQuery(): ScriptScoreQuery             = ScriptScoreQuery()
  def scriptScoreQuery(query: Query): ScriptScoreQuery = ScriptScoreQuery().query(query)

  def simpleStringQuery(q: String): SimpleStringQuery = SimpleStringQuery(q)
  def stringQuery(q: String): QueryStringQuery        = QueryStringQuery(q)

  @deprecated("use spanFirstQuery(query, end)", "7.7.0")
  def spanFirstQuery(query: SpanQuery) = new SpanFirstExpectsEnd(query)
  class SpanFirstExpectsEnd(query: SpanQuery) {
    def end(end: Int): SpanFirstQuery = SpanFirstQuery(query, end)
  }

  def spanFirstQuery(query: SpanQuery, end: Int) = SpanFirstQuery(query, end)

  def spanNearQuery(defs: Iterable[SpanQuery], slop: Int): SpanNearQuery =
    span.SpanNearQuery(defs.toSeq, slop)

  def spanOrQuery(iterable: Iterable[SpanQuery]): SpanOrQuery                             = span.SpanOrQuery(iterable.toSeq)
  def spanOrQuery(first: SpanQuery, rest: SpanQuery*): SpanOrQuery                        = spanOrQuery(first +: rest)
  def spanFieldMaskingQuery(fieldToMask: String, query: SpanQuery): SpanFieldMaskingQuery =
    span.SpanFieldMaskingQuery(fieldToMask, query)

  def spanContainingQuery(big: SpanQuery, little: SpanQuery): SpanContainingQuery =
    SpanContainingQuery(big, little)

  def spanWithinQuery(big: SpanQuery, little: SpanQuery): SpanWithinQuery =
    SpanWithinQuery(big, little)

  def spanTermQuery(field: String, value: Any): SpanTermQuery = SpanTermQuery(field, value)

  def spanNotQuery(include: SpanQuery, exclude: SpanQuery): SpanNotQuery =
    SpanNotQuery(include, exclude)

  def spanMultiTermQuery(query: MultiTermQuery): SpanMultiTermQuery = SpanMultiTermQuery(query)

  def termQuery(field: String, value: Any): TermQuery = TermQuery(field, value)

  def termsQuery[T](field: String, first: T, rest: T*): TermsQuery[T] =
    termsQuery(field, first +: rest)

  def termsQuery[T](field: String, values: Iterable[T]): TermsQuery[T] =
    TermsQuery(field, values)

  def termsLookupQuery(field: String, path: String, ref: DocumentRef): TermsLookupQuery =
    term.TermsLookupQuery(field, TermsLookup(ref, path))

  // Either minimumShouldMatchField or minimumShouldMatchScript should be specified, that's why they appear as mandatory parameters
  def termsSetQuery(field: String, terms: Set[String], minimumShouldMatchField: String): TermsSetQuery =
    TermsSetQuery(field, terms, minimumShouldMatchField = Some(minimumShouldMatchField))

  def termsSetQuery(field: String, terms: Set[String], minimumShouldMatchScript: Script): TermsSetQuery =
    TermsSetQuery(field, terms, minimumShouldMatchScript = Some(minimumShouldMatchScript))

  def wildcardQuery(field: String, value: Any): WildcardQuery = WildcardQuery(field, value)

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

  def sparseVectorQuery(field: String, inferenceId: String, query: String): SparseVectorQuery =
    SparseVectorQuery(field, inferenceId = Some(inferenceId), query = Some(query))

  def sparseVectorQuery(field: String, queryVector: Map[String, Double]): SparseVectorQuery =
    SparseVectorQuery(field, queryVector = queryVector)

  def semanticQuery(field: String, query: String): SemanticQuery =
    SemanticQuery(field, query)
}
