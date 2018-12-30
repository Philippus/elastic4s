package com.sksamuel.elastic4s.requests.searches.queries.funcscorer

import com.sksamuel.elastic4s.requests.searches.queries.Query

trait ScoreFunction {
  def filter: Option[Query]
}
