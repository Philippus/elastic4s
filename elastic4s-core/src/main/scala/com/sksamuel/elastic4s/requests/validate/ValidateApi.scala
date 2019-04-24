package com.sksamuel.elastic4s.requests.validate

import com.sksamuel.elastic4s.Indexes
import com.sksamuel.elastic4s.requests.searches.queries.Query

trait ValidateApi {

  def validateIn(indexes: Indexes): ValidateExpectsQuery = new ValidateExpectsQuery(indexes)
  class ValidateExpectsQuery(indexes: Indexes) {
    def query(query: Query): ValidateRequest = ValidateRequest(indexes, query)
  }
}
