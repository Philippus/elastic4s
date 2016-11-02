package com.sksamuel.elastic4s.search.queries.funcscorer

import org.elasticsearch.index.query.functionscore.{GaussDecayFunctionBuilder, ScoreFunctionBuilders}

case class GaussianDecayScoreDefinition(field: String,
                                        origin: String,
                                        scale: String)
  extends ScoreFunctionDefinition {
  override type B = GaussDecayFunctionBuilder
  def builder = ScoreFunctionBuilders.gaussDecayFunction(field, origin, scale)
}
