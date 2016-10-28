package com.sksamuel.elastic4s.search.query

import com.sksamuel.elastic4s.ScriptDefinition
import com.sksamuel.elastic4s.search.QueryDefinition
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
