package com.sksamuel.elastic4s.requests.searches.queries.matches

import com.sksamuel.elastic4s.requests.analyzers.Analyzer
import com.sksamuel.elastic4s.requests.common.Operator
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class MatchBoolPrefix(field: String,
                           value: Any,
                           analyzer: Option[String] = None,
                           queryName: Option[String] = None,
                           boost: Option[Double] = None,
                           minimumShouldMatch: Option[String] = None,
                           operator: Option[Operator] = None,
                           fuzziness: Option[String] = None,
                           prefixLength: Option[Int] = None,
                           maxExpansions: Option[Int] = None,
                           fuzzyTranspositions: Option[Boolean] = None,
                           fuzzyRewrite: Option[String] = None)
    extends Query {

  def analyzer(a: Analyzer): MatchBoolPrefix               = analyzer(a.name)
  def analyzer(name: String): MatchBoolPrefix              = copy(analyzer = name.some)
  def queryName(queryName: String): MatchBoolPrefix        = copy(queryName = queryName.some)
  def boost(boost: Double): MatchBoolPrefix                = copy(boost = boost.some)
  def minimumShouldMatch(minimum: String): MatchBoolPrefix = copy(minimumShouldMatch = minimum.some)
  def operator(operator: Operator): MatchBoolPrefix        = copy(operator = operator.some)
  def fuzziness(fuzziness: String): MatchBoolPrefix        = copy(fuzziness = fuzziness.some)
  def prefixLength(prefix: Int): MatchBoolPrefix           = copy(prefixLength = prefix.some)
  def maxExpansions(max: Int): MatchBoolPrefix             = copy(maxExpansions = max.some)
  def fuzzyTranspositions(fuzzy: Boolean): MatchBoolPrefix = copy(fuzzyTranspositions = fuzzy.some)
  def fuzzyRewrite(fuzzy: String): MatchBoolPrefix         = copy(fuzzyRewrite = fuzzy.some)
}
