package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.analyzers.Analyzer
import com.sksamuel.exts.OptionImplicits._

case class CommonTermsQueryDefinition(name: String,
                                      text: String,
                                      minimumShouldMatch: Option[Int] = None,
                                      lowFreqMinimumShouldMatch: Option[Int] = None,
                                      highFreqMinimumShouldMatch: Option[Int] = None,
                                      cutoffFrequency: Option[Double] = None,
                                      queryName: Option[String] = None,
                                      disableCoord: Option[Boolean] = None,
                                      boost: Option[Double] = None,
                                      highFreqOperator: Option[String] = None,
                                      lowFreqOperator: Option[String] = None,
                                      analyzer: Option[String] = None)
  extends QueryDefinition {

  def boost(boost: Double): CommonTermsQueryDefinition = copy(boost = Some(boost))
  def disableCoord(disableCoord: Boolean): CommonTermsQueryDefinition = copy(disableCoord = Some(disableCoord))
  def queryName(queryName: String): CommonTermsQueryDefinition = copy(queryName = Some(queryName))
  def cutoffFrequency(freq: Double): CommonTermsQueryDefinition = copy(cutoffFrequency = freq.some)

  def minimumShouldMatch(min: Int): CommonTermsQueryDefinition = copy(minimumShouldMatch = Some(min))
  def lowFreqMinimumShouldMatch(freq: Int): CommonTermsQueryDefinition = copy(lowFreqMinimumShouldMatch = Some(freq))
  def highFreqMinimumShouldMatch(freq: Int): CommonTermsQueryDefinition = copy(highFreqMinimumShouldMatch = Some(freq))
  def highFreqOperator(op: String): CommonTermsQueryDefinition = copy(highFreqOperator = Some(op))
  def lowFreqOperator(op: String): CommonTermsQueryDefinition = copy(lowFreqOperator = Some(op))
  def analyzer(a: Analyzer): CommonTermsQueryDefinition = analyzer(a.name)
  def analyzer(name: String): CommonTermsQueryDefinition = copy(analyzer = name.some)
}
