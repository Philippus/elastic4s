package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.analyzers.Analyzer

case class MatchPhraseDefinition(field: String,
                                 value: Any,
                                 boost: Option[Double] = None,
                                 analyzer: Option[String] = None,
                                 slop: Option[Int] = None,
                                 queryName: Option[String] = None) extends QueryDefinition {

  def analyzer(a: Analyzer): MatchPhraseDefinition = copy(analyzer = Some(a.name))
  def boost(boost: Double): MatchPhraseDefinition = copy(boost = Some(boost))
  def slop(slop: Int): MatchPhraseDefinition = copy(slop = Some(slop))
  def queryName(queryName: String): MatchPhraseDefinition = copy(queryName = Some(queryName))
}
