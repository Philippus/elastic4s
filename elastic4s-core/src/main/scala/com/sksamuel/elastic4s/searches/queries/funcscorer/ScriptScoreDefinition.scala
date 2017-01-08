package com.sksamuel.elastic4s.searches.queries.funcscorer

import com.sksamuel.elastic4s.script.ScriptDefinition
import org.elasticsearch.index.query.functionscore.{ScoreFunctionBuilders, ScriptScoreFunctionBuilder}
import com.sksamuel.exts.OptionImplicits._

case class ScriptScoreDefinition(script: ScriptDefinition,
                                 weight: Option[Double] = None) extends ScoreFunctionDefinition {

  override type B = ScriptScoreFunctionBuilder

  def builder = {
    val builder = ScoreFunctionBuilders.scriptFunction(script.build)
    weight.map(_.toFloat).foreach(builder.setWeight)
    builder
  }

  def weight(weight: Double): ScriptScoreDefinition = copy(weight = weight.some)
}
