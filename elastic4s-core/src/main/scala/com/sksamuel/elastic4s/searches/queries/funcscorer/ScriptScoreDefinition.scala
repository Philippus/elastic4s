package com.sksamuel.elastic4s.searches.queries.funcscorer

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.exts.OptionImplicits._

case class ScriptScoreDefinition(script: ScriptDefinition,
                                 weight: Option[Double] = None,
                                 override val filter: Option[QueryDefinition] = None) extends ScoreFunctionDefinition {
  def weight(weight: Double): ScriptScoreDefinition = copy(weight = weight.some)
  def filter(filter: QueryDefinition): ScriptScoreDefinition = copy(filter = filter.some)
}
