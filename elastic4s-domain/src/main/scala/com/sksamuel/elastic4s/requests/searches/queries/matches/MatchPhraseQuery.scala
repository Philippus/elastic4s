package com.sksamuel.elastic4s.requests.searches.queries.matches

import com.sksamuel.elastic4s.requests.analyzers.Analyzer
import com.sksamuel.elastic4s.requests.searches.queries.Query

case class MatchPhraseQuery(
    field: String,
    value: Any,
    boost: Option[Double] = None,
    analyzer: Option[String] = None,
    slop: Option[Int] = None,
    queryName: Option[String] = None,
    zeroTermsQuery: Option[ZeroTermsQuery] = None
) extends Query {
  def analyzer(a: Analyzer): MatchPhraseQuery                          = analyzer(a.name)
  def analyzer(analyzer: String): MatchPhraseQuery                     = copy(analyzer = Some(analyzer))
  def boost(boost: Double): MatchPhraseQuery                           = copy(boost = Some(boost))
  def slop(slop: Int): MatchPhraseQuery                                = copy(slop = Some(slop))
  def queryName(queryName: String): MatchPhraseQuery                   = copy(queryName = Some(queryName))
  def zeroTermsQuery(zeroTermsQuery: String): MatchPhraseQuery         =
    copy(zeroTermsQuery = Some(ZeroTermsQuery.valueOf(zeroTermsQuery)))
  def zeroTermsQuery(zeroTermsQuery: ZeroTermsQuery): MatchPhraseQuery = copy(zeroTermsQuery = Some(zeroTermsQuery))
}
