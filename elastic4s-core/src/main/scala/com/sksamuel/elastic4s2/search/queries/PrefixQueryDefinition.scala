package com.sksamuel.elastic4s2.search.queries

import com.sksamuel.elastic4s2.DefinitionAttributes.{DefinitionAttributeBoost, DefinitionAttributeRewrite}
import org.elasticsearch.index.query.QueryBuilders

case class PrefixQueryDefinition(field: String, prefix: Any)
  extends MultiTermQueryDefinition
    with DefinitionAttributeRewrite
    with DefinitionAttributeBoost {

  val builder = QueryBuilders.prefixQuery(field, prefix.toString)
  val _builder = builder

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}
