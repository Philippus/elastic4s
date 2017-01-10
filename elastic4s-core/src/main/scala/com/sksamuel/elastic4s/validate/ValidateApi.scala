package com.sksamuel.elastic4s.validate

import com.sksamuel.elastic4s.IndexesAndTypes
import com.sksamuel.elastic4s.searches.QueryDefinition

trait ValidateApi {

  def validateIn(indexesAndTypes: IndexesAndTypes): ValidateExpectsQuery = new ValidateExpectsQuery(indexesAndTypes)
  class ValidateExpectsQuery(indexesAndTypes: IndexesAndTypes) {
    def query(query: QueryDefinition): ValidateDefinition = ValidateDefinition(indexesAndTypes, query)
  }
}
