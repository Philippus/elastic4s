package com.sksamuel.elastic4s.searches.queries.funcscorer

import com.sksamuel.elastic4s.script.Script
import com.sksamuel.elastic4s.searches.queries.Query

trait ScoreApi {

  def randomScore(seed: Int) = RandomScoreFunction(seed)

  def scriptScore(script: Script) = ScriptScore(script)

  def gaussianScore(field: String, origin: String, scale: String) = GaussianDecayScore(field, origin, scale)

  def linearScore(field: String, origin: String, scale: String) = LinearDecayScore(field, origin, scale)

  def exponentialScore(field: String, origin: String, scale: String) =
    ExponentialDecayScore(field, origin, scale)

  def fieldFactorScore(field: String) = FieldValueFactor(field)

  def weightScore(boost: Double) = WeightScore(boost)
}
