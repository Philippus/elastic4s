package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.requests.script.Script

case class ScriptQuery(script: Script, boost: Option[Double] = None, queryName: Option[String] = None) extends Query {

  def queryName(queryName: String): ScriptQuery = copy(queryName = Some(queryName))
  def boost(boost: Double): ScriptQuery         = copy(boost = Some(boost))
}
