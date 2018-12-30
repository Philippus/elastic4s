package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.requests.searches.ScoreMode

case class NestedQuery(path: String,
                       query: Query,
                       scoreMode: Option[ScoreMode] = None,
                       boost: Option[Double] = None,
                       ignoreUnmapped: Option[Boolean] = None,
                       inner: Option[InnerHit] = None,
                       queryName: Option[String] = None)
    extends Query {
  require(query != null, "must specify query for nested score query")

  def boost(b: Double): NestedQuery                        = copy(boost = Option(b))
  def scoreMode(mode: String): NestedQuery                 = scoreMode(ScoreMode.valueOf(mode))
  def scoreMode(scoreMode: ScoreMode): NestedQuery         = copy(scoreMode = Option(scoreMode))
  def ignoreUnmapped(ignoreUnmapped: Boolean): NestedQuery = copy(ignoreUnmapped = Option(ignoreUnmapped))
  def inner(inner: InnerHit): NestedQuery                  = copy(inner = Option(inner))
  def queryName(queryName: String): NestedQuery            = copy(queryName = Option(queryName))
}
