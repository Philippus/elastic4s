package com.sksamuel.elastic4s.searches.queries.funcscorer

import com.sksamuel.elastic4s.ScriptBuilder
import org.elasticsearch.index.query.functionscore.{ScoreFunctionBuilders, ScriptScoreFunctionBuilder}

object ScriptScoreBuilderFn {
  def apply(d: ScriptScoreDefinition): ScriptScoreFunctionBuilder = {
    val builder = ScoreFunctionBuilders.scriptFunction(ScriptBuilder(d.script))
    d.weight.map(_.toFloat).foreach(builder.setWeight)
    builder
  }
}
