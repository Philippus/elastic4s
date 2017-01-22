package com.sksamuel.elastic4s.searches.queries

import org.apache.lucene.search.join.ScoreMode

case class NestedQueryDefinition(path: String,
                                 query: QueryDefinition,
                                 scoreMode: ScoreMode,
                                 boost: Option[Double] = None,
                                 ignoreUnmapped: Option[Boolean] = None,
                                 inner: Option[InnerHitDefinition] = None,
                                 queryName: Option[String] = None) extends QueryDefinition {
  require(query != null, "must specify query for nested score query")

  def boost(b: Double): NestedQueryDefinition = copy(boost = Option(b))
  def ignoreUnmapped(ignoreUnmapped: Boolean): NestedQueryDefinition = copy(ignoreUnmapped = Option(ignoreUnmapped))
  def inner(inner: InnerHitDefinition): NestedQueryDefinition = copy(inner = Option(inner))
  def queryName(queryName: String): NestedQueryDefinition = copy(queryName = Option(queryName))
}
