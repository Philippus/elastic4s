package com.sksamuel.elastic4s.searches.queries.funcscorer

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.exts.OptionImplicits._

case class ScriptScoreDefinition(script: ScriptDefinition,
                                 weight: Option[Double] = None) extends ScoreFunctionDefinition {
  def weight(weight: Double): ScriptScoreDefinition = copy(weight = weight.some)
}
