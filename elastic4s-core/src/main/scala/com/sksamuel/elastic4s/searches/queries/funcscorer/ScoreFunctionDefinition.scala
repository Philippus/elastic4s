package com.sksamuel.elastic4s.searches.queries.funcscorer

import com.sksamuel.elastic4s.searches.queries.QueryDefinition

trait ScoreFunctionDefinition {
  def filter: Option[QueryDefinition]
}
