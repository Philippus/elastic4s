package com.sksamuel.elastic4s.searches.queries.funcscorer

import com.sksamuel.elastic4s.EnumConversions
import org.elasticsearch.index.query.functionscore.{LinearDecayFunctionBuilder, ScoreFunctionBuilders}

object LinearDecayScoreBuilderFn {
  def apply(l: LinearDecayScoreDefinition): LinearDecayFunctionBuilder = {
    val builder = (l.offset, l.decay) match {
      case (Some(o), Some(d)) => ScoreFunctionBuilders.linearDecayFunction(l.field, l.origin, l.scale, o, d)
      case (Some(o), None)    => ScoreFunctionBuilders.linearDecayFunction(l.field, l.origin, l.scale, o)
      case _                  => ScoreFunctionBuilders.linearDecayFunction(l.field, l.origin, l.scale)
    }
    l.weight.map(_.toFloat).foreach(builder.setWeight)
    l.multiValueMode.map(EnumConversions.multiValueMode).foreach(builder.setMultiValueMode)
    builder
  }
}
