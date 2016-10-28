package com.sksamuel.elastic4s2.search.queries.funcscorer

import com.sksamuel.elastic4s2.ScriptDefinition
import org.elasticsearch.index.query.functionscore.ScriptScoreFunctionBuilder

case class ScriptScoreDefinition(script: ScriptDefinition) extends ScoreDefinition[ScriptScoreDefinition] {
  val builder = new ScriptScoreFunctionBuilder(script.toJavaAPI)
}
