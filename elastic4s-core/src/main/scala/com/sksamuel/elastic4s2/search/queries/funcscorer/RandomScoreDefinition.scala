package com.sksamuel.elastic4s2.search.queries.funcscorer

import org.elasticsearch.index.query.functionscore.RandomScoreFunctionBuilder

case class RandomScoreDefinition(seed: Int) extends ScoreDefinition[RandomScoreDefinition] {
  val builder = new RandomScoreFunctionBuilder().seed(seed)
}
