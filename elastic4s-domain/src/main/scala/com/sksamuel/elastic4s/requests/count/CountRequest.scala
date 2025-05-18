package com.sksamuel.elastic4s.requests.count

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.ext.OptionImplicits._
import com.sksamuel.elastic4s.requests.searches.queries.Query

case class CountRequest(
    indexes: Indexes,
    query: Option[Query] = None,
    allowNoIndices: Option[Boolean] = None,
    analyzeWildcard: Option[Boolean] = None,
    expandWildcards: Option[String] = None,
    ignoreUnavailable: Option[Boolean] = None,
    ignoreThrottled: Option[Boolean] = None,
    lenient: Option[Boolean] = None,
    routing: Option[String] = None,
    terminateAfter: Option[Int] = None,
    minScore: Option[Double] = None
) {
  def query(query: Query): CountRequest                      = copy(query = query.some)
  def lenient(lenient: Boolean): CountRequest                = copy(lenient = lenient.some)
  def allowNoIndices(allowNoIndices: Boolean): CountRequest  = copy(allowNoIndices = allowNoIndices.some)
  def ignoreThrottled(ignore: Boolean): CountRequest         = copy(ignoreThrottled = ignore.some)
  def ignoreUnavailable(ignore: Boolean): CountRequest       = copy(ignoreUnavailable = ignore.some)
  def analyzeWildcard(analyze: Boolean): CountRequest        = copy(analyzeWildcard = analyze.some)
  def expandWildcards(expandWildcards: String): CountRequest = copy(expandWildcards = expandWildcards.some)
  def routing(routing: String): CountRequest                 = copy(routing = routing.some)
  def terminateAfter(terminate_after: Int): CountRequest     = copy(terminateAfter = terminate_after.some)
  def minScore(minScore: Double): CountRequest               = copy(minScore = minScore.some)
}
