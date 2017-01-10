package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.DefinitionAttributes.{DefinitionAttributeBoost, DefinitionAttributeRewrite}
import org.elasticsearch.index.query.QueryBuilders

case class WildcardQueryDefinition(field: String, query: Any)
  extends QueryDefinition with MultiTermQueryDefinition
    with DefinitionAttributeRewrite
    with DefinitionAttributeBoost {

  val builder = QueryBuilders.wildcardQuery(field, query.toString)
  val _builder = builder

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}
