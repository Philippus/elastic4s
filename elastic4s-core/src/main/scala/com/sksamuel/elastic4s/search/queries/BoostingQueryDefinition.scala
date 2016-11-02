package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.search.QueryDefinition
import org.elasticsearch.index.query.{BoostingQueryBuilder, QueryBuilders}

case class BoostingQueryDefinition(positiveQuery: QueryDefinition,
                                   negativeQuery: QueryDefinition,
                                   queryName: Option[String] = None,
                                   boost: Option[Double] = None,
                                   negativeBoost: Option[Double] = None) extends QueryDefinition {

  def builder: BoostingQueryBuilder = {
    val builder = QueryBuilders.boostingQuery(positiveQuery.builder, negativeQuery.builder)
    boost.map(_.toFloat).foreach(builder.boost)
    negativeBoost.map(_.toFloat).foreach(builder.negativeBoost)
    queryName.foreach(builder.queryName)
    builder
  }

  def boost(boost: Double): BoostingQueryDefinition = copy(boost = Option(boost))
  def negativeBoost(negativeBoost: Double): BoostingQueryDefinition = copy(negativeBoost = Option(negativeBoost))
  def withQueryName(queryName: String) = copy(queryName = Option(queryName))
}
