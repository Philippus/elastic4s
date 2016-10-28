package com.sksamuel.elastic4s2.search.queries.funcscorer

import org.elasticsearch.index.query.functionscore.WeightBuilder

case class WeightScoreDefinition(boost: Double) extends ScoreDefinition[WeightScoreDefinition] {
  val builder = new WeightBuilder().setWeight(boost.toFloat)
}
