package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.exts.OptionImplicits._

case class FuzzyQuery(field: String,
                      termValue: Any,
                      fuzziness: Option[String] = None,
                      boost: Option[Double] = None,
                      transpositions: Option[Boolean] = None,
                      maxExpansions: Option[Int] = None,
                      prefixLength: Option[Int] = None,
                      queryName: Option[String] = None,
                      rewrite: Option[String] = None)
    extends MultiTermQuery {

  def fuzziness(fuzziness: String): FuzzyQuery            = copy(fuzziness = fuzziness.some)
  def boost(boost: Double): FuzzyQuery                    = copy(boost = boost.some)
  def transpositions(transpositions: Boolean): FuzzyQuery = copy(transpositions = transpositions.some)
  def maxExpansions(maxExpansions: Int): FuzzyQuery       = copy(maxExpansions = maxExpansions.some)
  def prefixLength(prefixLength: Int): FuzzyQuery         = copy(prefixLength = prefixLength.some)
  def queryName(queryName: String): FuzzyQuery            = copy(queryName = queryName.some)
  def rewrite(rewrite: String): FuzzyQuery                = copy(rewrite = rewrite.some)
}
