package com.sksamuel.elastic4s.queries

import org.elasticsearch.common.geo.GeoPoint
import org.elasticsearch.index.query._

import scala.language.{implicitConversions, reflectiveCalls}

/** @author Stephen Samuel */

trait QueryDsl {

  implicit def string2query(string: String): SimpleStringQueryDefinition = SimpleStringQueryDefinition(string)
  implicit def tuple2query(kv: (String, String)): TermQueryDefinition = TermQueryDefinition(kv._1, kv._2)

  def query = this

  def boostingQuery: BoostingQueryDefinition = new BoostingQueryDefinition

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

  def functionScoreQuery(query: QueryDefinition): FunctionScoreQueryDefinition = FunctionScoreQueryDefinition(query)

  def fuzzyQuery(name: String, value: Any) = FuzzyQueryDefinition(name, value)

  def geoBoxQuery(field: String) = GeoBoundingBoxQueryDefinition(field)
  def geoDistanceQuery(field: String): GeoDistanceQueryDefinition = GeoDistanceQueryDefinition(field)
  def geoHashCell(field: String, value: String): GeoHashCellQuery = GeoHashCellQuery(field).geohash(value)
  def geoPolygonQuery(field: String, points: Seq[GeoPoint]) = GeoPolygonQueryDefinition(field, points)

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

  def spanOrQuery: SpanOrQueryDefinition = new SpanOrQueryDefinition
  def spanTermQuery(field: String, value: Any): SpanTermQueryDefinition = new SpanTermQueryDefinition(field, value)
  def spanNotQuery: SpanNotQueryDefinition = new SpanNotQueryDefinition
  def spanNearQuery: SpanNearQueryDefinition = new SpanNearQueryDefinition

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
  def filter(first: QueryDefinition, rest:QueryDefinition*): BoolQueryDefinition = filter(first+:rest)
  def filter(queries: Iterable[QueryDefinition]): BoolQueryDefinition = new BoolQueryDefinition().filter(queries)
  def should(queries: QueryDefinition*): BoolQueryDefinition = new BoolQueryDefinition().should(queries: _*)
  def should(queries: Iterable[QueryDefinition]): BoolQueryDefinition = new BoolQueryDefinition().should(queries)
  def not(queries: QueryDefinition*): BoolQueryDefinition = new BoolQueryDefinition().not(queries: _*)
  def not(queries: Iterable[QueryDefinition]): BoolQueryDefinition = new BoolQueryDefinition().not(queries)
}



trait QueryDefinition {
  def builder: org.elasticsearch.index.query.QueryBuilder
}






































































































