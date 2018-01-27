package com.sksamuel.elastic4s.searches.queries.funcscorer

import org.elasticsearch.index.query.functionscore.{ScoreFunctionBuilder, ScoreFunctionBuilders}

object ScoreFunctionBuilderFn {
  def apply(func: ScoreFunctionDefinition): ScoreFunctionBuilder[_] =
    func match {
      case exp: ExponentialDecayScoreDefinition  => ExponentialDecayScoreBuilderFn(exp)
      case f: FieldValueFactorDefinition         => FieldValueFactorBuilderFn(f)
      case g: GaussianDecayScoreDefinition       => GaussianDecayScoreBuilderFn(g)
      case random: RandomScoreFunctionDefinition => RandomScoreFunctionBuilderFn(random)
      case linear: LinearDecayScoreDefinition    => LinearDecayScoreBuilderFn(linear)
      case script: ScriptScoreDefinition         => ScriptScoreBuilderFn(script)
      case WeightScoreDefinition(weight, _)      => ScoreFunctionBuilders.weightFactorFunction(weight.toFloat)
    }
}
