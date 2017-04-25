package com.sksamuel.elastic4s.searches.queries.funcscorer

import org.elasticsearch.index.query.functionscore.{ExponentialDecayFunctionBuilder, ScoreFunctionBuilders}

object ExponentialDecayScoreBuilderFn {
  def apply(exp: ExponentialDecayScoreDefinition): ExponentialDecayFunctionBuilder = {
    val builder = (exp.offset, exp.decay) match {
      case (Some(o), Some(d)) => ScoreFunctionBuilders.exponentialDecayFunction(exp.field, exp.origin, exp.scale, o, d)
      case (Some(o), None) => ScoreFunctionBuilders.exponentialDecayFunction(exp.field, exp.origin, exp.scale, o)
      case (None, Some(d)) => ScoreFunctionBuilders.exponentialDecayFunction(exp.field, exp.origin, exp.scale, null, d)
      case _ => ScoreFunctionBuilders.exponentialDecayFunction(exp.field, exp.origin, exp.scale)
    }
    exp.weight.map(_.toFloat).foreach(builder.setWeight)
    exp.multiValueMode.foreach(builder.setMultiValueMode)
    builder
  }
}
