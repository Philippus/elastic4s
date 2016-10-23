package com.sksamuel.elastic4s.queries

import com.sksamuel.elastic4s.DefinitionAttributes.DefinitionAttributeBoost
import org.elasticsearch.index.query.QueryBuilders

case class HasParentQueryDefinition(`type`: String, q: QueryDefinition)
  extends QueryDefinition with DefinitionAttributeBoost {

  val builder = QueryBuilders.hasParentQuery(`type`, q.builder)
  val _builder = builder

  def scoreMode(scoreMode: String): HasParentQueryDefinition = {
    builder.scoreMode(scoreMode)
    this
  }

  @deprecated("use scoreMode", "2.1.0")
  def scoreType(scoreType: String): HasParentQueryDefinition = {
    builder.scoreType(scoreType)
    this
  }

  def queryName(name: String) = {
    builder.queryName(name)
    this
  }
}
