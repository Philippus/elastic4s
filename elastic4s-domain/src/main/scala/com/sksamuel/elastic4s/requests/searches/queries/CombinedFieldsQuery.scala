package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.ext.OptionImplicits._
import com.sksamuel.elastic4s.requests.common.Operator
import com.sksamuel.elastic4s.requests.searches.queries.matches.ZeroTermsQuery

case class CombinedFieldsQuery(
    query: String,
    fields: Seq[(String, Option[Double])],
    autoGenerateSynonymsPhraseQuery: Option[Boolean] = None,
    operator: Option[Operator] = None,
    minimumShouldMatch: Option[String] = None,
    zeroTermsQuery: Option[ZeroTermsQuery] = None
) extends Query {

  def autoGenerateSynonymsPhraseQuery(value: Boolean): CombinedFieldsQuery =
    copy(autoGenerateSynonymsPhraseQuery = value.some)
  def operator(value: Operator): CombinedFieldsQuery                       = copy(operator = value.some)
  def minimumShouldMatch(minimumShouldMatch: String): CombinedFieldsQuery  =
    copy(minimumShouldMatch = minimumShouldMatch.some)
  def zeroTermsQuery(zeroTermsQuery: ZeroTermsQuery): CombinedFieldsQuery  = copy(zeroTermsQuery = zeroTermsQuery.some)

}
