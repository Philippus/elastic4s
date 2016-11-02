package com.sksamuel.elastic4s.searches.queries.funcscorer

import org.elasticsearch.index.query.functionscore.{ExponentialDecayFunctionBuilder, ScoreFunctionBuilders}

case class ExponentialDecayScoreDefinition(field: String, origin: String, scale: String)
  extends ScoreFunctionDefinition {
  override type B = ExponentialDecayFunctionBuilder
  def builder = ScoreFunctionBuilders.exponentialDecayFunction(field, origin, scale)
}
