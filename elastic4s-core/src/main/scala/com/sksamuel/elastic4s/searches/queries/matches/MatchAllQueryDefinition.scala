package com.sksamuel.elastic4s.searches.queries.matches

import com.sksamuel.elastic4s.searches.queries.QueryDefinition

case class MatchAllQueryDefinition(boost: Option[Float] = None,
                                   queryName: Option[String] = None) extends QueryDefinition {
  def boost(boost: Float): MatchAllQueryDefinition = copy(boost = Option(boost))
  def withBoost(boost: Float): MatchAllQueryDefinition = copy(boost = Option(boost))
  def queryName(queryName: String): MatchAllQueryDefinition = copy(queryName = Option(queryName))
  def withQueryName(queryName: String): MatchAllQueryDefinition = copy(queryName = Option(queryName))
}

case class MatchNoneQueryDefinition(queryName: Option[String] = None) extends QueryDefinition {
  def queryName(queryName: String): MatchNoneQueryDefinition = copy(queryName = Option(queryName))
  def withQueryName(queryName: String): MatchNoneQueryDefinition = copy(queryName = Option(queryName))
}
