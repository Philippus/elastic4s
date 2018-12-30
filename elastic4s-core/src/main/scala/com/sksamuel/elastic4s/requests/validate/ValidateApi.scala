package com.sksamuel.elastic4s.requests.validate

import com.sksamuel.elastic4s.IndexesAndTypes
import com.sksamuel.elastic4s.requests.searches.queries.Query

trait ValidateApi {

  def validateIn(indexesAndTypes: IndexesAndTypes): ValidateExpectsQuery = new ValidateExpectsQuery(indexesAndTypes)
  class ValidateExpectsQuery(indexesAndTypes: IndexesAndTypes) {
    def query(query: Query): ValidateRequest = ValidateRequest(indexesAndTypes, query)
  }
}
