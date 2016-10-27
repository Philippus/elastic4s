package com.sksamuel.elastic4s.query

import com.sksamuel.elastic4s.QueryDefinition
import org.elasticsearch.index.query.{MatchAllQueryBuilder, QueryBuilders}

case class MatchAllQueryDefinition(boost: Option[Float] = None,
                                   queryName: Option[String] = None) extends QueryDefinition {

  def builder: MatchAllQueryBuilder = {
    val builder = QueryBuilders.matchAllQuery
    boost.foreach(builder.boost)
    queryName.foreach(builder.queryName)
    builder
  }

  def boost(boost: Float) = copy(boost = Option(boost))
  def withBoost(boost: Float) = copy(boost = Option(boost))
  def withQueryName(queryName: String) = copy(queryName = Option(queryName))
}
