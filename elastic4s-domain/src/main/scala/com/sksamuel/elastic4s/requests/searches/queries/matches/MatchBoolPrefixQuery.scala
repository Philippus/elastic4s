package com.sksamuel.elastic4s.requests.searches.queries.matches

import com.sksamuel.elastic4s.requests.analyzers.Analyzer
import com.sksamuel.elastic4s.requests.common.Operator
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits.RichOptionImplicits

case class MatchBoolPrefixQuery(field: String,
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

  def analyzer(a: Analyzer): MatchBoolPrefixQuery = analyzer(a.name)
  def analyzer(name: String): MatchBoolPrefixQuery = copy(analyzer = name.some)
  def queryName(queryName: String): MatchBoolPrefixQuery = copy(queryName = queryName.some)
  def boost(boost: Double): MatchBoolPrefixQuery = copy(boost = boost.some)
  def minimumShouldMatch(minimum: String): MatchBoolPrefixQuery = copy(minimumShouldMatch = minimum.some)
  def operator(operator: Operator): MatchBoolPrefixQuery = copy(operator = operator.some)
  def fuzziness(fuzziness: String): MatchBoolPrefixQuery = copy(fuzziness = fuzziness.some)
  def prefixLength(prefix: Int): MatchBoolPrefixQuery = copy(prefixLength = prefix.some)
  def maxExpansions(max: Int): MatchBoolPrefixQuery = copy(maxExpansions = max.some)
  def fuzzyTranspositions(fuzzy: Boolean): MatchBoolPrefixQuery = copy(fuzzyTranspositions = fuzzy.some)
  def fuzzyRewrite(fuzzy: String): MatchBoolPrefixQuery = copy(fuzzyRewrite = fuzzy.some)
}
