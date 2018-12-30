package com.sksamuel.elastic4s.requests.searches.queries

import com.sksamuel.elastic4s.XContentBuilder
import com.sksamuel.elastic4s.requests.searches.queries.funcscorer.{ExponentialDecayScore, FieldValueFactor, GaussianDecayScore, LinearDecayScore, RandomScoreFunction, ScoreFunction, ScriptScore, WeightScore}

object ScoreFunctionBuilderFn {
  def apply(func: ScoreFunction): XContentBuilder =
    func match {
      case r: RandomScoreFunction   => RandomScoreFunctionBuilderFn(r)
      case g: GaussianDecayScore    => GaussianDecayScoreBuilderFn(g)
      case s: ScriptScore           => ScriptScoreBuilderFn(s)
      case f: FieldValueFactor      => FieldValueFactorBuilderFn(f)
      case e: ExponentialDecayScore => ExponentialDecayScoreBuilderFn(e)
      case w: WeightScore           => WeightBuilderFn(w)
      case l: LinearDecayScore      => LinearDecayScoreBuilderFn(l)
    }
}
