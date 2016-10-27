package com.sksamuel.elastic4s.query

import com.sksamuel.elastic4s.QueryDefinition
import org.elasticsearch.index.query.{NestedQueryBuilder, QueryBuilders}

case class NestedQueryDefinition(path: String,
                                 query: QueryDefinition,
                                 boost: Option[Double] = None,
                                 inner: Option[QueryInnerHitBuilder] = None,
                                 queryName: Option[String] = None,
                                 scoreMode: Option[String] = None) extends QueryDefinition {
  require(query != null, "must specify query for nested score query")

  def builder: NestedQueryBuilder = {
    val builder = QueryBuilders.nestedQuery(path, query.builder)
    boost.foreach(b => builder.boost(b.toFloat))
    scoreMode.foreach(builder.scoreMode)
    inner.foreach(builder.innerHit)
    queryName.foreach(builder.queryName)
    builder
  }

  def inner(name: String): NestedQueryDefinition = copy(inner = Option(new QueryInnerHitBuilder().setName(name)))
  def inner(inner: QueryInnerHitsDefinition): NestedQueryDefinition = copy(inner = Option(inner.builder))

  def scoreMode(scoreMode: String): NestedQueryDefinition = copy(scoreMode = Option(scoreMode))
  def boost(b: Double): NestedQueryDefinition = copy(boost = Option(b))
  def queryName(queryName: String): NestedQueryDefinition = copy(queryName = Option(queryName))
}
