package com.sksamuel.elastic4s.requests.searches.queries.matches

import com.sksamuel.elastic4s.requests.analyzers.Analyzer
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits.RichOptionImplicits

case class MatchPhrasePrefixQuery(field: String,
                                  value: Any,
                                  analyzer: Option[String] = None,
                                  queryName: Option[String] = None,
                                  boost: Option[Double] = None,
                                  maxExpansions: Option[Int] = None,
                                  slop: Option[Int] = None)
  extends Query {

  def analyzer(a: Analyzer): MatchPhrasePrefixQuery = analyzer(a.name)
  def analyzer(name: String): MatchPhrasePrefixQuery = copy(analyzer = name.some)
  def queryName(queryName: String): MatchPhrasePrefixQuery = copy(queryName = queryName.some)
  def boost(boost: Double): MatchPhrasePrefixQuery = copy(boost = boost.some)
  def maxExpansions(max: Int): MatchPhrasePrefixQuery = copy(maxExpansions = max.some)
  def slop(slop: Int): MatchPhrasePrefixQuery = copy(slop = slop.some)
}
