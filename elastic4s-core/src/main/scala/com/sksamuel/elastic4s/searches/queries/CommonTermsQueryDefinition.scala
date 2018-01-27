package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.analyzers.Analyzer
import com.sksamuel.exts.OptionImplicits._

case class CommonTermsQueryDefinition(name: String,
                                      text: String,
                                      minimumShouldMatch: Option[String] = None,
                                      lowFreqMinimumShouldMatch: Option[String] = None,
                                      highFreqMinimumShouldMatch: Option[String] = None,
                                      cutoffFrequency: Option[Double] = None,
                                      queryName: Option[String] = None,
                                      boost: Option[Double] = None,
                                      highFreqOperator: Option[String] = None,
                                      lowFreqOperator: Option[String] = None,
                                      analyzer: Option[String] = None)
    extends QueryDefinition {

  def boost(boost: Double): CommonTermsQueryDefinition          = copy(boost = Some(boost))
  def queryName(queryName: String): CommonTermsQueryDefinition  = copy(queryName = Some(queryName))
  def cutoffFrequency(freq: Double): CommonTermsQueryDefinition = copy(cutoffFrequency = freq.some)

  def minimumShouldMatch(min: Int): CommonTermsQueryDefinition = copy(minimumShouldMatch = Some(min.toString))
  def lowFreqMinimumShouldMatch(freq: Int): CommonTermsQueryDefinition =
    copy(lowFreqMinimumShouldMatch = Some(freq.toString))
  def highFreqMinimumShouldMatch(freq: Int): CommonTermsQueryDefinition =
    copy(highFreqMinimumShouldMatch = Some(freq.toString))
  def minimumShouldMatch(min: String): CommonTermsQueryDefinition         = copy(minimumShouldMatch = Some(min))
  def lowFreqMinimumShouldMatch(freq: String): CommonTermsQueryDefinition = copy(lowFreqMinimumShouldMatch = Some(freq))
  def highFreqMinimumShouldMatch(freq: String): CommonTermsQueryDefinition =
    copy(highFreqMinimumShouldMatch = Some(freq))
  def highFreqOperator(op: String): CommonTermsQueryDefinition = copy(highFreqOperator = Some(op))
  def lowFreqOperator(op: String): CommonTermsQueryDefinition  = copy(lowFreqOperator = Some(op))
  def analyzer(a: Analyzer): CommonTermsQueryDefinition        = analyzer(a.name)
  def analyzer(name: String): CommonTermsQueryDefinition       = copy(analyzer = name.some)
}
