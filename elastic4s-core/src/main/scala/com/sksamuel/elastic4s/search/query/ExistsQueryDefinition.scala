package com.sksamuel.elastic4s.search.query

import com.sksamuel.elastic4s.search.QueryDefinition
import org.elasticsearch.index.query.{ExistsQueryBuilder, QueryBuilders}

case class ExistsQueryDefinition(field: String,
                                 boost: Option[Float] = None,
                                 queryName: Option[String] = None) extends QueryDefinition {

  def builder: ExistsQueryBuilder = {
    val builder = QueryBuilders.existsQuery(field)
    boost.foreach(builder.boost)
    queryName.foreach(builder.queryName)
    builder
  }

  def withBoost(boost: Float) = copy(boost = Option(boost))
  def withQueryName(queryName: String) = copy(queryName = Option(queryName))
}
