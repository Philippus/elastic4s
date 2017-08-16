package com.sksamuel.elastic4s.searches.queries.funcscorer

import com.sksamuel.elastic4s.EnumConversions
import org.elasticsearch.index.query.functionscore.{GaussDecayFunctionBuilder, ScoreFunctionBuilders}

object GaussianDecayScoreBuilderFn {
  def apply(g: GaussianDecayScoreDefinition): GaussDecayFunctionBuilder = {
    val builder = (g.offset, g.decay) match {
      case (Some(o), Some(d)) => ScoreFunctionBuilders.gaussDecayFunction(g.field, g.origin, g.scale, o, d)
      case (Some(o), None) => ScoreFunctionBuilders.gaussDecayFunction(g.field, g.origin, g.scale, o)
      case _ => ScoreFunctionBuilders.gaussDecayFunction(g.field, g.origin, g.scale)
    }
    g.weight.map(_.toFloat).foreach(builder.setWeight)
    g.multiValueMode.map(EnumConversions.multiValueMode).foreach(builder.setMultiValueMode)
    builder
  }
}
