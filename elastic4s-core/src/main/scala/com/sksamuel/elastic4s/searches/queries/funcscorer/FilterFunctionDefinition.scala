package com.sksamuel.elastic4s.searches.queries.funcscorer

import com.sksamuel.elastic4s.searches.queries.QueryDefinition

case class FilterFunctionDefinition(score: ScoreFunctionDefinition,
                                    filter: Option[QueryDefinition] = None) {
  def filter(query: QueryDefinition): FilterFunctionDefinition = FilterFunctionDefinition(score, Some(query))
}
