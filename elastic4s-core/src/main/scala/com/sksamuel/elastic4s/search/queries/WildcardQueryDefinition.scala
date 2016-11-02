package com.sksamuel.elastic4s.search.queries

import com.sksamuel.elastic4s.DefinitionAttributes.{DefinitionAttributeBoost, DefinitionAttributeRewrite}
import com.sksamuel.elastic4s.search.QueryDefinition
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
