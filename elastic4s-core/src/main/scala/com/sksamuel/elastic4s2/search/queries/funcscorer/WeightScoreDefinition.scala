package com.sksamuel.elastic4s2.search.queries.funcscorer

import org.elasticsearch.index.query.functionscore.{ScoreFunctionBuilders, WeightBuilder}

case class WeightScoreDefinition(weight: Double)
  extends ScoreFunctionDefinition {

  override type B = WeightBuilder

  def builder = ScoreFunctionBuilders.weightFactorFunction(weight.toFloat)
}
