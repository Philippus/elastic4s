package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.searches.{QueryBuilderFn, QueryDefinition}
import org.elasticsearch.index.query.QueryBuilders
import com.sksamuel.exts.OptionImplicits._

case class DisMaxDefinition(queries: Seq[QueryDefinition],
                            boost: Option[Double] = None,
                            tieBreaker: Option[Double] = None,
                            queryName: Option[String] = None
                           ) extends QueryDefinition {

  def builder = {
    val builder = QueryBuilders.disMaxQuery()
    queries.foreach(q => builder.add(QueryBuilderFn(q)))
    boost.map(_.toFloat).foreach(builder.boost)
    tieBreaker.map(_.toFloat).foreach(builder.tieBreaker)
    queryName.foreach(builder.queryName)
    builder
  }

  def boost(boost: Double): DisMaxDefinition = copy(boost = boost.some)
  def queryName(queryName: String): DisMaxDefinition = copy(queryName = queryName.some)
  def tieBreaker(tieBreaker: Double): DisMaxDefinition = copy(tieBreaker = tieBreaker.some)
}
