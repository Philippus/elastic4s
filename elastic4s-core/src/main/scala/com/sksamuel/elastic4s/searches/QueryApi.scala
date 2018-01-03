package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.elastic4s.searches.queries._
import com.sksamuel.elastic4s.searches.queries.funcscorer.FunctionScoreQueryDefinition
import com.sksamuel.elastic4s.searches.queries.geo._
import com.sksamuel.elastic4s.searches.queries.matches._
import com.sksamuel.elastic4s.searches.queries.span._
import com.sksamuel.elastic4s.searches.queries.term._
import com.sksamuel.elastic4s.{DistanceUnit, DocumentRef, Indexable}

import scala.language.{implicitConversions, reflectiveCalls}

trait QueryApi {

  implicit def string2query(string: String): SimpleStringQueryDefinition = SimpleStringQueryDefinition(string)
  implicit def tuple2query(kv: (String, String)): TermQueryDefinition = TermQueryDefinition(kv._1, kv._2)

  def boostingQuery(positiveQuery: QueryDefinition,
                    negativeQuery: QueryDefinition): BoostingQueryDefinition = BoostingQueryDefinition(positiveQuery, negativeQuery)

  @deprecated("use commonTermsQuery", "6.1.2")
  def commonQuery(field: String) = new CommonQueryExpectsText(field)
  def commonTermsQuery(field: String) = new CommonQueryExpectsText(field)

  class CommonQueryExpectsText(name: String) {
    def text(q: String): CommonTermsQueryDefinition = CommonTermsQueryDefinition(name, q)
    def query(q: String): CommonTermsQueryDefinition = text(q)
  }

  @deprecated("use commonTermsQuery", "6.1.2")
  def commonQuery(field: String, text: String) = CommonTermsQueryDefinition(field, text)
  def commonTermsQuery(field: String, text: String) = CommonTermsQueryDefinition(field, text)

  def constantScoreQuery(query: QueryDefinition): ConstantScoreDefinition = ConstantScoreDefinition(query)

  def dismax(first: QueryDefinition, rest: QueryDefinition*): DisMaxQueryDefinition = dismax(first +: rest)
  def dismax(queries: Iterable[QueryDefinition]): DisMaxQueryDefinition = DisMaxQueryDefinition(queries.toSeq)

  def existsQuery(field: String) = ExistsQueryDefinition(field)

  @deprecated("use termQuery or termsQuery with _field_names", "6.1.0")
  def fieldNamesQuery(first: String, rest: String*)
                     (implicit builder: BuildableTermsQuery[String]): TermsQueryDefinition[String] =
    fieldNamesQuery(first +: rest)

  @deprecated("use termQuery or termsQuery with _field_names", "6.1.0")
  def fieldNamesQuery(names: Iterable[String])
                     (implicit builder: BuildableTermsQuery[String]): TermsQueryDefinition[String] =
    termsQuery("_field_names", names)

  @deprecated("Use bool query directly", "5.3.3")
  def filter(first: QueryDefinition, rest: QueryDefinition*): BoolQueryDefinition = filter(first +: rest)

  @deprecated("Use bool query directly", "5.3.3")
  def filter(queries: Iterable[QueryDefinition]): BoolQueryDefinition = BoolQueryDefinition().filter(queries)

  def fuzzyQuery(field: String, value: String): FuzzyQueryDefinition = FuzzyQueryDefinition(field, value)

  def functionScoreQuery(): FunctionScoreQueryDefinition = FunctionScoreQueryDefinition()
  def functionScoreQuery(query: QueryDefinition): FunctionScoreQueryDefinition = functionScoreQuery().query(query)

  def geoBoxQuery(field: String) = GeoBoundingBoxQueryDefinition(field)
  def geoBoxQuery(field: String, topleft: String, bottomright: String): GeoBoundingBoxQueryDefinition =
    GeoBoundingBoxQueryDefinition(field).withGeohash(topleft, bottomright)

  def geoDistanceQuery(field: String): GeoDistanceExpectsPoint = new GeoDistanceExpectsPoint(field)
  def geoDistanceQuery(field: String, geohash: String): GeoDistanceQueryDefinition = GeoDistanceQueryDefinition(field).geohash(geohash)
  def geoDistanceQuery(field: String, lat: Double, long: Double): GeoDistanceQueryDefinition = GeoDistanceQueryDefinition(field).point(lat, long)

  @deprecated("use geoDistanceQuery(field, hash) or geoDistanceQuery(field, lat, long)", "6.1.2")
  class GeoDistanceExpectsPoint(field: String) {
    def geohash(geohash: String): GeoDistanceQueryDefinition = GeoDistanceQueryDefinition(field).geohash(geohash)
    def point(lat: Double, long: Double): GeoDistanceQueryDefinition =
      GeoDistanceQueryDefinition(field).point(lat, long)
  }

  class GeoDistanceExpectsDistance(gdef: GeoDistanceQueryDefinition) {
    def distance(distance: String): GeoDistanceQueryDefinition = gdef.distance(distance)
    def distance(distance: Double, unit: DistanceUnit): GeoDistanceQueryDefinition = gdef.distance(distance, unit)
  }

  def geoHashCell(field: String, geohash: String): GeoHashCellQueryDefinition =
    GeoHashCellQueryDefinition(field).geohash(geohash)

  def geoHashCell(field: String, point: GeoPoint): GeoHashCellQueryDefinition =
    GeoHashCellQueryDefinition(field).point(point)

  def geoPolygonQuery(field: String) = new GeoPolygonExpectsPoints(field)
  class GeoPolygonExpectsPoints(field: String) {
    def points(first: GeoPoint, rest: GeoPoint*): GeoPolygonQueryDefinition = points(first +: rest)
    def points(points: Iterable[GeoPoint]): GeoPolygonQueryDefinition = geoPolygonQuery(field, points)
  }

  def geoPolygonQuery(field: String, first: GeoPoint, rest: GeoPoint*): GeoPolygonQueryDefinition =
    geoPolygonQuery(field, first +: rest)

  def geoPolygonQuery(field: String,
                      points: Iterable[GeoPoint]): GeoPolygonQueryDefinition =
    GeoPolygonQueryDefinition(field, points.toSeq)

  def geoShapeQuery(field: String, shape: Shape): GeoShapeQueryDefinition = GeoShapeQueryDefinition(field, shape)

  def hasChildQuery(`type`: String): HasChildQueryExpectsQuery = new HasChildQueryExpectsQuery(`type`)

  def hasChildQuery(`type`: String, query: QueryDefinition, scoreMode: ScoreMode): HasChildQueryDefinition =
    HasChildQueryDefinition(`type`, query, scoreMode)

  class HasChildQueryExpectsQuery(`type`: String) {
    def query(q: QueryDefinition): ExpectsScoreMode = new ExpectsScoreMode(q)
    def query(q: String): ExpectsScoreMode = new ExpectsScoreMode(q)
    class ExpectsScoreMode(q: QueryDefinition) {
      def scoreMode(mode: String): HasChildQueryDefinition = scoreMode(ScoreMode.valueOf(mode))
      def scoreMode(scoreMode: ScoreMode): HasChildQueryDefinition = hasChildQuery(`type`, q, scoreMode)
    }
  }

  def hasParentQuery(`type`: String) = new HasParentQueryExpectsQuery(`type`)

  def hasParentQuery(`type`: String, query: QueryDefinition, score: Boolean) =
    HasParentQueryDefinition(`type`, query, score)

  class HasParentQueryExpectsQuery(`type`: String) {
    def query(q: QueryDefinition) = new ExpectsScoreMode(q)
    class ExpectsScoreMode(q: QueryDefinition) {
      def scoreMode(scoreMode: Boolean): HasParentQueryDefinition = hasParentQuery(`type`, q, scoreMode)
    }
  }

  def innerHits(name: String): InnerHitDefinition = InnerHitDefinition(name)

  @deprecated("use matchQuery(field, value) instead of the tupled version", "5.2.0")
  def matchQuery(tuple: (String, Any)): MatchQueryDefinition = MatchQueryDefinition(tuple._1, tuple._2)
  def matchQuery(field: String, value: Any): MatchQueryDefinition = MatchQueryDefinition(field, value)

  def matchPhraseQuery(field: String, value: Any): MatchPhraseDefinition = MatchPhraseDefinition(field, value)

  def matchPhrasePrefixQuery(field: String, value: Any) = MatchPhrasePrefixDefinition(field, value)

  def multiMatchQuery(text: String) = MultiMatchQueryDefinition(text)

  def matchNoneQuery() = MatchNoneQueryDefinition()
  def matchAllQuery() = MatchAllQueryDefinition()

  def moreLikeThisQuery(first: String, rest: String*): MoreLikeThisExpectsLikes = moreLikeThisQuery(first +: rest)
  def moreLikeThisQuery(fields: Iterable[String]): MoreLikeThisExpectsLikes = new MoreLikeThisExpectsLikes(fields.toSeq)

  class MoreLikeThisExpectsLikes(fields: Seq[String]) {

    def likeTexts(first: String, rest: String*): MoreLikeThisQueryDefinition = likeTexts(first +: rest)

    def likeTexts(texts: Iterable[String]): MoreLikeThisQueryDefinition =
      MoreLikeThisQueryDefinition(fields, texts.toSeq)

    def likeItems(first: MoreLikeThisItem, rest: MoreLikeThisItem*): MoreLikeThisQueryDefinition =
      likeItems(first +: rest)

    def likeItems(items: Iterable[MoreLikeThisItem]): MoreLikeThisQueryDefinition =
      MoreLikeThisQueryDefinition(fields).copy(likeDocs = items.toSeq)

    def likeDocs(first: DocumentRef,
                 rest: DocumentRef*): MoreLikeThisQueryDefinition = likeDocs(first +: rest)

    def likeDocs(docs: Iterable[DocumentRef]): MoreLikeThisQueryDefinition =
      likeItems(docs.map { d => MoreLikeThisItem(d) })

    def artificialDocs(first: ArtificialDocument,
                       rest: ArtificialDocument*): MoreLikeThisQueryDefinition = artificialDocs(first +: rest)

    def artificialDocs(docs: Iterable[ArtificialDocument]): MoreLikeThisQueryDefinition =
      MoreLikeThisQueryDefinition(fields).copy(artificialDocs = docs.toSeq)
  }

  def nestedQuery(path: String) = new NestedQueryExpectsQuery(path)
  class NestedQueryExpectsQuery(path: String) {
    class NestedQueryExpectsScoreMode(query: QueryDefinition)
    def query(query: QueryDefinition): NestedQueryDefinition = nestedQuery(path, query)
  }
  def nestedQuery(path: String, query: QueryDefinition): NestedQueryDefinition = NestedQueryDefinition(path, query)

  def query(queryString: String): QueryStringQueryDefinition = queryStringQuery(queryString)
  def queryStringQuery(queryString: String): QueryStringQueryDefinition = QueryStringQueryDefinition(queryString)

  def percolateQuery(`type`: String, field: String = "query") = new PercolateExpectsUsing(`type`, field)
  class PercolateExpectsUsing(`type`: String, field: String) {
    def usingId(index: String, `type`: String, id: Any): PercolateQueryDefinition =
      usingId(DocumentRef(index, `type`, id.toString))

    def usingId(ref: DocumentRef): PercolateQueryDefinition =
      PercolateQueryDefinition(field, `type`, ref = Some(ref))

    def usingSource(json: String): PercolateQueryDefinition =
      PercolateQueryDefinition(field, `type`, source = Some(json))

    def usingSource[T](t: T)(implicit indexable: Indexable[T]): PercolateQueryDefinition =
      PercolateQueryDefinition(field, `type`, source = Some(indexable.json(t)))
  }

  def rangeQuery(field: String): RangeQuery = RangeQuery(field)

  def rawQuery(json: String): RawQueryDefinition = RawQueryDefinition(json)

  @deprecated("use the non-tupled version regexQuery(field,value)", "6.1.2")
  def regexQuery(tuple: (String, String)): RegexQueryDefinition = regexQuery(tuple._1, tuple._2)
  def regexQuery(field: String, value: String): RegexQueryDefinition = RegexQueryDefinition(field, value)

  @deprecated("use the non-tupled version prefixQuery(field,value)", "6.1.2")
  def prefixQuery(tuple: (String, Any)): PrefixQuery = prefixQuery(tuple._1, tuple._2)
  def prefixQuery(field: String, value: Any): PrefixQuery = PrefixQuery(field, value)

  def scriptQuery(script: ScriptDefinition): ScriptQueryDefinition = ScriptQueryDefinition(script)
  def scriptQuery(script: String): ScriptQueryDefinition = ScriptQueryDefinition(script)

  def simpleStringQuery(q: String): SimpleStringQueryDefinition = SimpleStringQueryDefinition(q)
  def stringQuery(q: String): QueryStringQueryDefinition = QueryStringQueryDefinition(q)

  def spanFirstQuery(query: SpanQueryDefinition) = new SpanFirstExpectsEnd(query)
  class SpanFirstExpectsEnd(query: SpanQueryDefinition) {
    def end(end: Int) = SpanFirstQueryDefinition(query, end)
  }

  def spanNearQuery(defs: Iterable[SpanQueryDefinition], slop: Int): SpanNearQueryDefinition =
    SpanNearQueryDefinition(defs.toSeq, slop)

  def spanOrQuery(iterable: Iterable[SpanQueryDefinition]): SpanOrQueryDefinition = SpanOrQueryDefinition(iterable.toSeq)
  def spanOrQuery(first: SpanQueryDefinition, rest: SpanQueryDefinition*): SpanOrQueryDefinition = spanOrQuery(first +: rest)

  def spanContainingQuery(big: SpanQueryDefinition, little: SpanQueryDefinition): SpanContainingQueryDefinition =
    SpanContainingQueryDefinition(big, little)

  def spanWithinQuery(big: SpanQueryDefinition, little: SpanQueryDefinition): SpanWithinQueryDefinition =
    SpanWithinQueryDefinition(big, little)

  def spanTermQuery(field: String, value: Any): SpanTermQueryDefinition = SpanTermQueryDefinition(field, value)

  def spanNotQuery(include: SpanQueryDefinition, exclude: SpanQueryDefinition): SpanNotQueryDefinition =
    SpanNotQueryDefinition(include, exclude)

  def spanMultiTermQuery(query: MultiTermQueryDefinition) = SpanMultiTermQueryDefinition(query)

  @deprecated("use the non-tupled version termQuery(field,value)", "6.1.2")
  def termQuery(tuple: (String, Any)): TermQueryDefinition = termQuery(tuple._1, tuple._2)
  def termQuery(field: String, value: Any): TermQueryDefinition = TermQueryDefinition(field, value)

  def termsQuery[T: BuildableTermsQuery](field: String,
                                         first: T, rest: T*): TermsQueryDefinition[T] = termsQuery(field, first +: rest)

  def termsQuery[T](field: String, values: Iterable[T])
                   (implicit buildable: BuildableTermsQuery[T]) = TermsQueryDefinition(field, values)

  def termsLookupQuery(field: String, path: String, ref: DocumentRef) =
    TermsLookupQueryDefinition(field, TermsLookup(ref, path))

  def termsSetQuery(field: String, terms: Set[Any]): com.sksamuel.elastic4s.searches.queries.term.TermsSetQuery =
    com.sksamuel.elastic4s.searches.queries.term.TermsSetQuery(field, terms)

  @deprecated("use the non-tupled version wildcardQuery(field,value)", "6.1.2")
  def wildcardQuery(tuple: (String, Any)): WildcardQueryDefinition = wildcardQuery(tuple._1, tuple._2)
  def wildcardQuery(field: String, value: Any): WildcardQueryDefinition = WildcardQueryDefinition(field, value)

  def typeQuery(`type`: String) = TypeQueryDefinition(`type`)

  def idsQuery(ids: Iterable[Any]): IdQuery = IdQuery(ids.toSeq)
  def idsQuery(id: Any, rest: Any*): IdQuery = IdQuery(id +: rest)

  // -- bool query dsl ---
  @deprecated("this usage leads to subtle bugs, please use boolQuery().must(...).should(...).not(...)", "5.0.0")
  def bool(block: => BoolQueryDefinition): BoolQueryDefinition = block

  def bool(mustQueries: Seq[QueryDefinition],
           shouldQueries: Seq[QueryDefinition],
           notQueries: Seq[QueryDefinition]): BoolQueryDefinition = {
    must(mustQueries).should(shouldQueries).not(notQueries)
  }

  // convenience to make an emtpy bool which can be appended to
  def boolQuery(): BoolQueryDefinition = BoolQueryDefinition()

  // short cut for a boolean query with musts
  def must(first: QueryDefinition, rest: QueryDefinition*): BoolQueryDefinition = must(first +: rest)
  def must(queries: Iterable[QueryDefinition]): BoolQueryDefinition = BoolQueryDefinition().must(queries)

  // short cut for a boolean query with shoulds
  def should(queries: QueryDefinition*): BoolQueryDefinition = BoolQueryDefinition().should(queries: _*)
  def should(queries: Iterable[QueryDefinition]): BoolQueryDefinition = BoolQueryDefinition().should(queries)

  // short cut for a boolean query with nots
  def not(queries: QueryDefinition*): BoolQueryDefinition = BoolQueryDefinition().not(queries: _*)
  def not(queries: Iterable[QueryDefinition]): BoolQueryDefinition = BoolQueryDefinition().not(queries)
}
