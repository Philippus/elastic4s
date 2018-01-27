package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.searches.ScoreMode

case class NestedQueryDefinition(path: String,
                                 query: QueryDefinition,
                                 scoreMode: Option[ScoreMode] = None,
                                 boost: Option[Double] = None,
                                 ignoreUnmapped: Option[Boolean] = None,
                                 inner: Option[InnerHitDefinition] = None,
                                 queryName: Option[String] = None)
    extends QueryDefinition {
  require(query != null, "must specify query for nested score query")

  def boost(b: Double): NestedQueryDefinition                        = copy(boost = Option(b))
  def scoreMode(mode: String): NestedQueryDefinition                 = scoreMode(ScoreMode.valueOf(mode))
  def scoreMode(scoreMode: ScoreMode): NestedQueryDefinition         = copy(scoreMode = Option(scoreMode))
  def ignoreUnmapped(ignoreUnmapped: Boolean): NestedQueryDefinition = copy(ignoreUnmapped = Option(ignoreUnmapped))
  def inner(inner: InnerHitDefinition): NestedQueryDefinition        = copy(inner = Option(inner))
  def queryName(queryName: String): NestedQueryDefinition            = copy(queryName = Option(queryName))
}
