package com.sksamuel.elastic4s.searches.queries.funcscorer

import org.elasticsearch.index.query.functionscore.{RandomScoreFunctionBuilder, ScoreFunctionBuilders}

object RandomScoreFunctionBuilderFn {
  def apply(random: RandomScoreFunctionDefinition): RandomScoreFunctionBuilder = {
    val builder = ScoreFunctionBuilders.randomFunction().seed(random.seed)
    random.weight.map(_.toFloat).foreach(builder.setWeight)
    builder
  }
}
