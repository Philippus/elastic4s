package com.sksamuel.elastic4s.requests.searches.queries.matches

import com.sksamuel.elastic4s.requests.analyzers.Analyzer
import com.sksamuel.elastic4s.requests.common.Operator
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class MatchQuery(field: String,
                      value: Any,
                      analyzer: Option[String] = None,
                      boost: Option[Double] = None,
                      cutoffFrequency: Option[Double] = None,
                      fuzziness: Option[String] = None,
                      fuzzyRewrite: Option[String] = None,
                      fuzzyTranspositions: Option[Boolean] = None,
                      lenient: Option[Boolean] = None,
                      maxExpansions: Option[Int] = None,
                      minimumShouldMatch: Option[String] = None,
                      operator: Option[Operator] = None,
                      prefixLength: Option[Int] = None,
                      queryName: Option[String] = None,
                      zeroTerms: Option[String] = None)
    extends Query {

  def analyzer(an: String): MatchQuery   = copy(analyzer = an.some)
  def analyzer(an: Analyzer): MatchQuery = copy(analyzer = an.name.some)

  def boost(boost: Double): MatchQuery               = copy(boost = boost.some)
  def cutoffFrequency(f: Double): MatchQuery         = copy(cutoffFrequency = f.some)
  def lenient(lenient: Boolean): MatchQuery          = copy(lenient = lenient.some)
  def fuzziness(fuzziness: String): MatchQuery       = copy(fuzziness = fuzziness.some)
  def fuzzyRewrite(fuzzyRewrite: String): MatchQuery = copy(fuzzyRewrite = fuzzyRewrite.some)
  def prefixLength(prefixLength: Int): MatchQuery    = copy(prefixLength = prefixLength.some)

  @deprecated("use lenient(Boolean)", "5.0.0")
  def setLenient(l: Boolean): MatchQuery = lenient(l)

  def fuzzyTranspositions(f: Boolean): MatchQuery =
    copy(fuzzyTranspositions = f.some)

  def maxExpansions(max: Int): MatchQuery = copy(maxExpansions = max.some)

  def minimumShouldMatch(min: String): MatchQuery = copy(minimumShouldMatch = min.some)

  def withAndOperator(): MatchQuery      = operator("AND")
  def withOrOperator(): MatchQuery       = operator("OR")
  def operator(op: String): MatchQuery   = copy(operator = Operator.valueOf(op.toUpperCase).some)
  def operator(op: Operator): MatchQuery = copy(operator = op.some)

  def queryName(queryName: String): MatchQuery = copy(queryName = queryName.some)

  def zeroTermsQuery(zeroTerms: String): MatchQuery = copy(zeroTerms = zeroTerms.some)
}
