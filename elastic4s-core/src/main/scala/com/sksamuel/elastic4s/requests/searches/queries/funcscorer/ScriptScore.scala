package com.sksamuel.elastic4s.requests.searches.queries.funcscorer

import com.sksamuel.elastic4s.requests.script.Script
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.exts.OptionImplicits._

case class ScriptScore(script: Script, weight: Option[Double] = None, override val filter: Option[Query] = None)
    extends ScoreFunction {
  def weight(weight: Double): ScriptScore = copy(weight = weight.some)
  def filter(filter: Query): ScriptScore  = copy(filter = filter.some)
}
