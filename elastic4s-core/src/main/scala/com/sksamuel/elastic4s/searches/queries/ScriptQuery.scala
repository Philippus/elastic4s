package com.sksamuel.elastic4s.searches.queries

import com.sksamuel.elastic4s.script.Script
import com.sksamuel.exts.OptionImplicits._

case class ScriptQuery(script: Script,
                       boost: Option[Double] = None,
                       queryName: Option[String] = None)
    extends Query {

  def queryName(queryName: String): ScriptQuery = copy(queryName = queryName.some)
  def boost(boost: Double): ScriptQuery         = copy(boost = boost.some)
}
