package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.requests.analyzers.Analyzer
import com.sksamuel.exts.OptionImplicits._

case class CommonTermsQuery(name: String,
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
    extends Query {

  def boost(boost: Double): CommonTermsQuery          = copy(boost = Some(boost))
  def queryName(queryName: String): CommonTermsQuery  = copy(queryName = Some(queryName))
  def cutoffFrequency(freq: Double): CommonTermsQuery = copy(cutoffFrequency = freq.some)

  def minimumShouldMatch(min: Int): CommonTermsQuery = copy(minimumShouldMatch = Some(min.toString))
  def lowFreqMinimumShouldMatch(freq: Int): CommonTermsQuery =
    copy(lowFreqMinimumShouldMatch = Some(freq.toString))
  def highFreqMinimumShouldMatch(freq: Int): CommonTermsQuery =
    copy(highFreqMinimumShouldMatch = Some(freq.toString))
  def minimumShouldMatch(min: String): CommonTermsQuery         = copy(minimumShouldMatch = Some(min))
  def lowFreqMinimumShouldMatch(freq: String): CommonTermsQuery = copy(lowFreqMinimumShouldMatch = Some(freq))
  def highFreqMinimumShouldMatch(freq: String): CommonTermsQuery =
    copy(highFreqMinimumShouldMatch = Some(freq))
  def highFreqOperator(op: String): CommonTermsQuery = copy(highFreqOperator = Some(op))
  def lowFreqOperator(op: String): CommonTermsQuery  = copy(lowFreqOperator = Some(op))
  def analyzer(a: Analyzer): CommonTermsQuery        = analyzer(a.name)
  def analyzer(name: String): CommonTermsQuery       = copy(analyzer = name.some)
}
