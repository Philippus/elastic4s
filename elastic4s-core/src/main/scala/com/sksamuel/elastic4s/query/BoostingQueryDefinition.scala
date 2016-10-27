package com.sksamuel.elastic4s.query

import com.sksamuel.elastic4s.QueryDefinition
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
}
