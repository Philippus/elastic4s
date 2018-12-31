package com.sksamuel.elastic4s.requests.searches.queries.matches

import com.sksamuel.elastic4s.requests.analyzers.Analyzer
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class MatchPhrasePrefix(field: String,
                             value: Any,
                             analyzer: Option[String] = None,
                             queryName: Option[String] = None,
                             boost: Option[Double] = None,
                             maxExpansions: Option[Int] = None,
                             slop: Option[Int] = None)
    extends Query {

  def analyzer(a: Analyzer): MatchPhrasePrefix        = analyzer(a.name)
  def analyzer(name: String): MatchPhrasePrefix       = copy(analyzer = name.some)
  def queryName(queryName: String): MatchPhrasePrefix = copy(queryName = queryName.some)
  def boost(boost: Double): MatchPhrasePrefix         = copy(boost = boost.some)
  def maxExpansions(max: Int): MatchPhrasePrefix      = copy(maxExpansions = max.some)
  def slop(slop: Int): MatchPhrasePrefix              = copy(slop = slop.some)
}
