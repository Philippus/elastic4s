package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.exts.OptionImplicits._

case class BoolQuery(adjustPureNegative: Option[Boolean] = None,
                     boost: Option[Double] = None,
                     minimumShouldMatch: Option[String] = None,
                     queryName: Option[String] = None,
                     filters: Seq[Query] = Nil,
                     must: Seq[Query] = Nil,
                     not: Seq[Query] = Nil,
                     should: Seq[Query] = Nil)
    extends Query {

  def adjustPureNegative(adjustPureNegative: Boolean): BoolQuery =
    copy(adjustPureNegative = adjustPureNegative.some)

  def boost(boost: Double): BoolQuery =
    copy(boost = boost.some)

  def filter(first: Query, rest: Query*): BoolQuery = filter(first +: rest)
  def filter(queries: Iterable[Query]): BoolQuery   = copy(filters = queries.toSeq)

  def minimumShouldMatch(min: Int): BoolQuery    = copy(minimumShouldMatch = min.toString.some)
  def minimumShouldMatch(min: String): BoolQuery = copy(minimumShouldMatch = min.some)

  /**
    * Appends the given queries to the existing not queries.
    */
  def withNot(first: Query, rest: Query*): BoolQuery = withNot(first +: rest)
  @deprecated("use withNot", "6.0.0")
  def appendNot(first: Query, rest: Query*): BoolQuery = appendNot(first +: rest)

  /**
    * Appends the given queries to the existing not queries.
    */
  def withNot(queries: Iterable[Query]): BoolQuery = appendNot(queries)
  @deprecated("use withNot", "6.0.0")
  def appendNot(queries: Iterable[Query]): BoolQuery = copy(not = not ++ queries.toSeq)

  /**
    * Appends the current 'must' queries with the given queries.
    */
  def withMust(first: Query, rest: Query*): BoolQuery = withMust(first +: rest)
  @deprecated("use withMust", "6.0.0")
  def appendMust(first: Query, rest: Query*): BoolQuery = appendMust(first +: rest)

  /**
    * Appends the current 'must' queries with the given queries.
    */
  def withMust(queries: Iterable[Query]): BoolQuery = appendMust(queries)
  @deprecated("use withMust", "6.0.0")
  def appendMust(queries: Iterable[Query]): BoolQuery = copy(must = must ++ queries.toIndexedSeq)

  /**
    * Replaces the current 'must' queries with the given queries.
    */
  def must(queries: Query*): BoolQuery = must(queries)

  /**
    * Replaces the current 'must' queries with the given queries.
    */
  def must(queries: Iterable[Query]): BoolQuery = copy(must = queries.toIndexedSeq)

  /**
    * Replaces the current 'not' queries with the given queries.
    */
  def not(queries: Query*): BoolQuery = not(queries)

  /**
    * Replaces the current 'not' queries with the given queries.
    */
  def not(queries: Iterable[Query]): BoolQuery = copy(not = queries.toSeq)

  /**
    * Appends the current 'should' queries with the given queries.
    */
  def withShould(first: Query, rest: Query*): BoolQuery = withShould(first +: rest)
  @deprecated("use withMust", "6.0.0")
  def appendShould(first: Query, rest: Query*): BoolQuery = appendShould(first +: rest)

  /**
    * Appends the current 'should' queries with the given queries.
    */
  def withShould(queries: Iterable[Query]): BoolQuery = appendShould(queries)
  @deprecated("use withMust", "6.0.0")
  def appendShould(queries: Iterable[Query]): BoolQuery = copy(should = should ++ queries.toSeq)

  /**
    * Replaces the current 'should' queries with the given queries.
    */
  def should(queries: Query*): BoolQuery = should(queries)

  /**
    * Replaces the current 'should' queries with the given queries.
    */
  def should(queries: Iterable[Query]): BoolQuery = copy(should = queries.toSeq)

  def queryName(queryName: String): BoolQuery = copy(queryName = queryName.some)
}
