package com.sksamuel.elastic4s.searches.queries.funcscorer

import com.sksamuel.elastic4s.script.ScriptDefinition
import com.sksamuel.elastic4s.searches.queries.QueryDefinition

trait ScoreApi {

  implicit class RichScorer(scorer: ScoreFunctionDefinition) {
    def filter(query: QueryDefinition) = filterFunction(scorer).filter(query)
  }

  def filterFunction(scorer: ScoreFunctionDefinition): FilterFunctionDefinition = FilterFunctionDefinition(scorer, None)

  def randomScore(seed: Int) = RandomScoreFunctionDefinition(seed)

  def scriptScore(script: ScriptDefinition) = ScriptScoreDefinition(script)

  def gaussianScore(field: String, origin: String, scale: String) = GaussianDecayScoreDefinition(field, origin, scale)

  def linearScore(field: String, origin: String, scale: String) = LinearDecayScoreDefinition(field, origin, scale)

  def exponentialScore(field: String, origin: String, scale: String) =
    ExponentialDecayScoreDefinition(field, origin, scale)

  def fieldFactorScore(field: String) = FieldValueFactorDefinition(field)

  def weightScore(boost: Double) = WeightScoreDefinition(boost)
}
