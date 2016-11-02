package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.search.QueryDefinition
import org.apache.lucene.search.join.ScoreMode
import org.elasticsearch.index.query.{NestedQueryBuilder, QueryBuilders}

case class NestedQueryDefinition(path: String,
                                 query: QueryDefinition,
                                 scoreMode: ScoreMode,
                                 boost: Option[Double] = None,
                                 ignoreUnmapped: Option[Boolean] = None,
                                 inner: Option[InnerHitDefinition] = None,
                                 queryName: Option[String] = None) extends QueryDefinition {
  require(query != null, "must specify query for nested score query")

  def builder: NestedQueryBuilder = {
    val builder = QueryBuilders.nestedQuery(path, query.builder, scoreMode)
    boost.map(_.toFloat).map(builder.boost)
    inner.map(_.builder).foreach(builder.innerHit)
    queryName.foreach(builder.queryName)
    ignoreUnmapped.foreach(builder.ignoreUnmapped)
    builder
  }

  def boost(b: Double): NestedQueryDefinition = copy(boost = Option(b))
  def ignoreUnmapped(ignoreUnmapped: Boolean): NestedQueryDefinition = copy(ignoreUnmapped = Option(ignoreUnmapped))
  def inner(inner: InnerHitDefinition): NestedQueryDefinition = copy(inner = Option(inner))
  def queryName(queryName: String): NestedQueryDefinition = copy(queryName = Option(queryName))
}
