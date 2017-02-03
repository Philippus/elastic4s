package com.sksamuel.elastic4s.searches.queries.matches

import com.sksamuel.elastic4s.analyzers.Analyzer
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.exts.OptionImplicits._

case class MatchPhrasePrefixDefinition(field: String,
                                       value: Any,
                                       analyzer: Option[String] = None,
                                       queryName: Option[String] = None,
                                       boost: Option[Double] = None,
                                       maxExpansions: Option[Int] = None,
                                       slop: Option[Int] = None)
  extends QueryDefinition {

  def analyzer(a: Analyzer): MatchPhrasePrefixDefinition = analyzer(a.name)
  def analyzer(name: String): MatchPhrasePrefixDefinition = copy(analyzer = name.some)
  def queryName(queryName: String): MatchPhrasePrefixDefinition = copy(queryName = queryName.some)
  def boost(boost: Double): MatchPhrasePrefixDefinition = copy(boost = boost.some)
  def maxExpansions(max: Int): MatchPhrasePrefixDefinition = copy(maxExpansions = max.some)
  def slop(slop: Int): MatchPhrasePrefixDefinition = copy(slop = slop.some)
}
