package com.sksamuel.elastic4s2.search.queries.funcscorer

import org.elasticsearch.index.query.functionscore.{LinearDecayFunctionBuilder, ScoreFunctionBuilders}

case class LinearDecayScoreDefinition(field: String, origin: String, scale: String)
  extends ScoreFunctionDefinition {
  override type B = LinearDecayFunctionBuilder
  def builder = ScoreFunctionBuilders.linearDecayFunction(field, origin, scale)
}
