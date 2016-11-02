package com.sksamuel.elastic4s2.search.queries.funcscorer

import com.sksamuel.elastic4s2.ScriptDefinition
import org.elasticsearch.index.query.functionscore.{ScoreFunctionBuilders, ScriptScoreFunctionBuilder}

case class ScriptScoreDefinition(script: ScriptDefinition)
  extends ScoreFunctionDefinition {

  override type B = ScriptScoreFunctionBuilder

  def builder = ScoreFunctionBuilders.scriptFunction(script.toJavaAPI)
}
