package com.sksamuel.elastic4s.requests.searches.queries.matches

import com.sksamuel.elastic4s.requests.analyzers.Analyzer
import com.sksamuel.elastic4s.requests.searches.queries.Query

case class MatchPhrase(field: String,
                       value: Any,
                       boost: Option[Double] = None,
                       analyzer: Option[String] = None,
                       slop: Option[Int] = None,
                       queryName: Option[String] = None)
    extends Query {

  def analyzer(a: Analyzer): MatchPhrase        = copy(analyzer = Some(a.name))
  def boost(boost: Double): MatchPhrase         = copy(boost = Some(boost))
  def slop(slop: Int): MatchPhrase              = copy(slop = Some(slop))
  def queryName(queryName: String): MatchPhrase = copy(queryName = Some(queryName))
}
