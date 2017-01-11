package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.exts.OptionImplicits._
import org.elasticsearch.index.query.MultiTermQueryBuilder

@deprecated("Fuzzy queries are not useful enough and will be removed in a future version", "5.0.0")
case class FuzzyQueryDefinition(field: String,
                                termValue: Any,
                                fuzziness: Option[String] = None,
                                boost: Option[Double] = None,
                                transpositions: Option[Boolean] = None,
                                maxExpansions: Option[Int] = None,
                                prefixLength: Option[Int] = None,
                                queryName: Option[String] = None,
                                rewrite: Option[String] = None
                               )
  extends MultiTermQueryDefinition {

  def boost(boost: Double): FuzzyQueryDefinition = copy(boost = boost.some)
  def prefixLength(prefixLength: Int): FuzzyQueryDefinition = copy(prefixLength = prefixLength.some)
  def rewrite(rewrite: String): FuzzyQueryDefinition = copy(rewrite = rewrite.some)
  def queryName(queryName: String): FuzzyQueryDefinition = copy(queryName = queryName.some)
  def fuzziness(fuzziness: String): FuzzyQueryDefinition = copy(fuzziness = fuzziness.some)
  def transpositions(transpositions: Boolean): FuzzyQueryDefinition = copy(transpositions = transpositions.some)
  def maxExpansions(maxExpansions: Int): FuzzyQueryDefinition = copy(maxExpansions = maxExpansions.some)
  override def builder: MultiTermQueryBuilder = ???
}
