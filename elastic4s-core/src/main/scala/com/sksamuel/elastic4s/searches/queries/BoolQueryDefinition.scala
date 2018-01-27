package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.exts.OptionImplicits._

case class BoolQueryDefinition(adjustPureNegative: Option[Boolean] = None,
                               boost: Option[Double] = None,
                               minimumShouldMatch: Option[String] = None,
                               queryName: Option[String] = None,
                               filters: Seq[QueryDefinition] = Nil,
                               must: Seq[QueryDefinition] = Nil,
                               not: Seq[QueryDefinition] = Nil,
                               should: Seq[QueryDefinition] = Nil)
    extends QueryDefinition {

  def adjustPureNegative(adjustPureNegative: Boolean): BoolQueryDefinition =
    copy(adjustPureNegative = adjustPureNegative.some)

  def boost(boost: Double): BoolQueryDefinition =
    copy(boost = boost.some)

  def filter(first: QueryDefinition, rest: QueryDefinition*): BoolQueryDefinition = filter(first +: rest)
  def filter(queries: Iterable[QueryDefinition]): BoolQueryDefinition             = copy(filters = queries.toSeq)

  def minimumShouldMatch(min: Int): BoolQueryDefinition    = copy(minimumShouldMatch = min.toString.some)
  def minimumShouldMatch(min: String): BoolQueryDefinition = copy(minimumShouldMatch = min.some)

  /**
    * Appends the given queries to the existing not queries.
    */
  def withNot(first: QueryDefinition, rest: QueryDefinition*): BoolQueryDefinition = withNot(first +: rest)
  @deprecated("use withNot", "6.0.0")
  def appendNot(first: QueryDefinition, rest: QueryDefinition*): BoolQueryDefinition = appendNot(first +: rest)

  /**
    * Appends the given queries to the existing not queries.
    */
  def withNot(queries: Iterable[QueryDefinition]): BoolQueryDefinition = appendNot(queries)
  @deprecated("use withNot", "6.0.0")
  def appendNot(queries: Iterable[QueryDefinition]): BoolQueryDefinition = copy(not = not ++ queries.toSeq)

  /**
    * Appends the current 'must' queries with the given queries.
    */
  def withMust(first: QueryDefinition, rest: QueryDefinition*): BoolQueryDefinition = withMust(first +: rest)
  @deprecated("use withMust", "6.0.0")
  def appendMust(first: QueryDefinition, rest: QueryDefinition*): BoolQueryDefinition = appendMust(first +: rest)

  /**
    * Appends the current 'must' queries with the given queries.
    */
  def withMust(queries: Iterable[QueryDefinition]): BoolQueryDefinition = appendMust(queries)
  @deprecated("use withMust", "6.0.0")
  def appendMust(queries: Iterable[QueryDefinition]): BoolQueryDefinition = copy(must = must ++ queries.toIndexedSeq)

  /**
    * Replaces the current 'must' queries with the given queries.
    */
  def must(queries: QueryDefinition*): BoolQueryDefinition = must(queries)

  /**
    * Replaces the current 'must' queries with the given queries.
    */
  def must(queries: Iterable[QueryDefinition]): BoolQueryDefinition = copy(must = queries.toIndexedSeq)

  /**
    * Replaces the current 'not' queries with the given queries.
    */
  def not(queries: QueryDefinition*): BoolQueryDefinition = not(queries)

  /**
    * Replaces the current 'not' queries with the given queries.
    */
  def not(queries: Iterable[QueryDefinition]): BoolQueryDefinition = copy(not = queries.toSeq)

  /**
    * Appends the current 'should' queries with the given queries.
    */
  def withShould(first: QueryDefinition, rest: QueryDefinition*): BoolQueryDefinition = withShould(first +: rest)
  @deprecated("use withMust", "6.0.0")
  def appendShould(first: QueryDefinition, rest: QueryDefinition*): BoolQueryDefinition = appendShould(first +: rest)

  /**
    * Appends the current 'should' queries with the given queries.
    */
  def withShould(queries: Iterable[QueryDefinition]): BoolQueryDefinition = appendShould(queries)
  @deprecated("use withMust", "6.0.0")
  def appendShould(queries: Iterable[QueryDefinition]): BoolQueryDefinition = copy(should = should ++ queries.toSeq)

  /**
    * Replaces the current 'should' queries with the given queries.
    */
  def should(queries: QueryDefinition*): BoolQueryDefinition = should(queries)

  /**
    * Replaces the current 'should' queries with the given queries.
    */
  def should(queries: Iterable[QueryDefinition]): BoolQueryDefinition = copy(should = queries.toSeq)

  def queryName(queryName: String): BoolQueryDefinition = copy(queryName = queryName.some)
}
