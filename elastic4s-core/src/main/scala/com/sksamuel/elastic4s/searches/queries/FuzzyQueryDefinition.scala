package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.exts.OptionImplicits._

case class FuzzyQueryDefinition(field: String,
                                termValue: Any,
                                fuzziness: Option[String] = None,
                                boost: Option[Double] = None,
                                transpositions: Option[Boolean] = None,
                                maxExpansions: Option[Int] = None,
                                prefixLength: Option[Int] = None,
                                queryName: Option[String] = None,
                                rewrite: Option[String] = None)
    extends MultiTermQueryDefinition {

  def fuzziness(fuzziness: String): FuzzyQueryDefinition            = copy(fuzziness = fuzziness.some)
  def boost(boost: Double): FuzzyQueryDefinition                    = copy(boost = boost.some)
  def transpositions(transpositions: Boolean): FuzzyQueryDefinition = copy(transpositions = transpositions.some)
  def maxExpansions(maxExpansions: Int): FuzzyQueryDefinition       = copy(maxExpansions = maxExpansions.some)
  def prefixLength(prefixLength: Int): FuzzyQueryDefinition         = copy(prefixLength = prefixLength.some)
  def queryName(queryName: String): FuzzyQueryDefinition            = copy(queryName = queryName.some)
  def rewrite(rewrite: String): FuzzyQueryDefinition                = copy(rewrite = rewrite.some)
}
