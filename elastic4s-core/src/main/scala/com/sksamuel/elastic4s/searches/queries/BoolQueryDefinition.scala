package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.exts.OptionImplicits._

case class BoolQueryDefinition(
                                adjustPureNegative: Option[Boolean] = None,
                                boost: Option[Double] = None,
                                disableCoord: Option[Boolean] = None,
                                minimumShouldMatch: Option[String] = None,
                                queryName: Option[String] = None,
                                filters: Seq[QueryDefinition] = Nil,
                                must: Seq[QueryDefinition] = Nil,
                                not: Seq[QueryDefinition] = Nil,
                                should: Seq[QueryDefinition] = Nil
                              ) extends QueryDefinition {

  def adjustPureNegative(adjustPureNegative: Boolean): BoolQueryDefinition =
    copy(adjustPureNegative = adjustPureNegative.some)

  def boost(boost: Double): BoolQueryDefinition =
    copy(boost = boost.some)

  def disableCoord(disableCoord: Boolean): BoolQueryDefinition =
    copy(disableCoord = disableCoord.some)

  def filter(first: QueryDefinition, rest: QueryDefinition*): BoolQueryDefinition = filter(first +: rest)
  def filter(queries: Iterable[QueryDefinition]): BoolQueryDefinition = copy(filters = queries.toSeq)

  def minimumShouldMatch(min: Int): BoolQueryDefinition = copy(minimumShouldMatch = min.toString.some)
  def minimumShouldMatch(min: String): BoolQueryDefinition = copy(minimumShouldMatch = min.some)

  /**
    * Appends the current 'not' queries with the given queries.
    */
  def appendNot(first: QueryDefinition, rest: QueryDefinition*): BoolQueryDefinition = appendNot(first +: rest)

  /**
    * Appends the current 'not' queries with the given queries.
    */
  def appendNot(queries: Iterable[QueryDefinition]): BoolQueryDefinition = copy(not = not ++ queries.toSeq)

  /**
    * Appends the current 'must' queries with the given queries.
    */
  def appendMust(first: QueryDefinition, rest: QueryDefinition*): BoolQueryDefinition = appendMust(first +: rest)

  /**
    * Appends the current 'must' queries with the given queries.
    */
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
  def appendShould(first: QueryDefinition, rest: QueryDefinition*): BoolQueryDefinition = appendShould(first +: rest)

  /**
    * Appends the current 'should' queries with the given queries.
    */
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
