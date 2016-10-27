package com.sksamuel.elastic4s.query

import com.sksamuel.elastic4s.{QueryDefinition, ScriptDefinition}
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
