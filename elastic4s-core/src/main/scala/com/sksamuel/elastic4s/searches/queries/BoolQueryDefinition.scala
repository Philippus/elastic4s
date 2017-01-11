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

  def must(queries: QueryDefinition*): BoolQueryDefinition = must(queries)
  def must(queries: Iterable[QueryDefinition]): BoolQueryDefinition = copy(must = queries.toIndexedSeq)

  def not(queries: QueryDefinition*): BoolQueryDefinition = not(queries)
  def not(queries: Iterable[QueryDefinition]): BoolQueryDefinition = copy(not = queries.toSeq)

  def should(queries: QueryDefinition*): BoolQueryDefinition = should(queries)
  def should(queries: Iterable[QueryDefinition]): BoolQueryDefinition = copy(should = queries.toSeq)

  def queryName(queryName: String): BoolQueryDefinition = copy(queryName = queryName.some)
}
