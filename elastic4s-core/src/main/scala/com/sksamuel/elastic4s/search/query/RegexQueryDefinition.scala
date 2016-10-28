package com.sksamuel.elastic4s.search.query

import com.sksamuel.elastic4s.DefinitionAttributes.{DefinitionAttributeBoost, DefinitionAttributeRewrite}
import org.elasticsearch.index.query.{QueryBuilders, RegexpFlag}

case class RegexQueryDefinition(field: String, regex: Any)
  extends MultiTermQueryDefinition
    with DefinitionAttributeRewrite
    with DefinitionAttributeBoost {

  val builder = QueryBuilders.regexpQuery(field, regex.toString)
  val _builder = builder

  def flags(flags: RegexpFlag*): RegexQueryDefinition = {
    builder.flags(flags: _*)
    this
  }

  def queryName(queryName: String): this.type = {
    builder.queryName(queryName)
    this
  }
}
