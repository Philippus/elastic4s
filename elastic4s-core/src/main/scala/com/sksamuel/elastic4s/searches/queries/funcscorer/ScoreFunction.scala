package com.sksamuel.elastic4s.searches.queries.funcscorer

import com.sksamuel.elastic4s.searches.queries.Query

trait ScoreFunction {
  def filter: Option[Query]
}
