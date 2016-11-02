package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.elastic4s.searches.QueryDefinition
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
