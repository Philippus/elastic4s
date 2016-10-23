package com.sksamuel.elastic4s.queries

import com.sksamuel.elastic4s.ScriptDefinition
import org.elasticsearch.index.query.QueryBuilders

case class ScriptQueryDefinition(script: ScriptDefinition)
  extends QueryDefinition {

  val builder = QueryBuilders.scriptQuery(script.toJavaAPI)
  val _builder = builder

  def queryName(queryName: String): ScriptQueryDefinition = {
    builder.queryName(queryName)
    this
  }
}
