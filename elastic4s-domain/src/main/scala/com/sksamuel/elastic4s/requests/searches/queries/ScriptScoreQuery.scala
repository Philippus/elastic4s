package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.requests.script.Script

case class ScriptScoreQuery(
    query: Option[Query] = None,
    script: Option[Script] = None,
    minScore: Option[Double] = None,
    boost: Option[Double] = None
) extends Query {
  def boost(boost: Double): ScriptScoreQuery   = copy(boost = Option(boost))
  def minScore(min: Double): ScriptScoreQuery  = copy(minScore = Option(min))
  def query(query: Query): ScriptScoreQuery    = copy(query = Some(query))
  def script(script: Script): ScriptScoreQuery = copy(script = Some(script))
}
