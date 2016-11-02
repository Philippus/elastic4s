package com.sksamuel.elastic4s2.search.queries.funcscorer

import org.elasticsearch.index.query.functionscore.{RandomScoreFunctionBuilder, ScoreFunctionBuilders}

case class RandomScoreDefinition(seed: Long) extends ScoreFunctionDefinition {
  override type B = RandomScoreFunctionBuilder
  def builder = ScoreFunctionBuilders.randomFunction(seed)
}
