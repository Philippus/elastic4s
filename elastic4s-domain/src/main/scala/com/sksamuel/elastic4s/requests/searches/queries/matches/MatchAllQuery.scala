package com.sksamuel.elastic4s.requests.searches.queries.matches

import com.sksamuel.elastic4s.requests.searches.queries.Query

case class MatchAllQuery(boost: Option[Double] = None, queryName: Option[String] = None) extends Query {
  def boost(boost: Double): MatchAllQuery             = copy(boost = Option(boost))
  def withBoost(boost: Double): MatchAllQuery         = copy(boost = Option(boost))
  def queryName(queryName: String): MatchAllQuery     = copy(queryName = Option(queryName))
  def withQueryName(queryName: String): MatchAllQuery = copy(queryName = Option(queryName))
}

case class MatchNoneQuery(queryName: Option[String] = None) extends Query {
  def queryName(queryName: String): MatchNoneQuery     = copy(queryName = Option(queryName))
  def withQueryName(queryName: String): MatchNoneQuery = copy(queryName = Option(queryName))
}
