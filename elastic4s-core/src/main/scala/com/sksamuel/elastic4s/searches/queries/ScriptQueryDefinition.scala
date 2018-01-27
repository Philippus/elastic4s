package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.exts.OptionImplicits._

case class ScriptQueryDefinition(script: ScriptDefinition,
                                 boost: Option[Double] = None,
                                 queryName: Option[String] = None)
    extends QueryDefinition {

  def queryName(queryName: String): ScriptQueryDefinition = copy(queryName = queryName.some)
  def boost(boost: Double): ScriptQueryDefinition         = copy(boost = boost.some)
}
