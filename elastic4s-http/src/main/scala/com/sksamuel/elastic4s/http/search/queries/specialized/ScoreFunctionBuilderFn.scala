package com.sksamuel.elastic4s.http.search.queries.specialized

import com.sksamuel.elastic4s.json.XContentBuilder
import com.sksamuel.elastic4s.searches.queries.funcscorer.{GaussianDecayScoreDefinition, ScoreFunctionDefinition}

object ScoreFunctionBuilderFn {
  def apply(func: ScoreFunctionDefinition): XContentBuilder = {
    func match {
      case g: GaussianDecayScoreDefinition => GaussianDecayScoreBuilderFn(g)
    }
  }
}
