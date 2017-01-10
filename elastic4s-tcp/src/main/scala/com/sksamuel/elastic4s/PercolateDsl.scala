package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.indexes.IndexDefinition
import com.sksamuel.elastic4s.searches.{QueryBuilderFn, QueryDefinition}

trait PercolateDsl {
  self: ElasticDsl =>

  def register(query: QueryDefinition) = new RegisterExpectsInto(query)
  class RegisterExpectsInto(query: QueryDefinition) {
    def into(indexType: IndexAndType, field: String = "query"): IndexDefinition = {
      val src = s""" { "$field" : ${QueryBuilderFn(query).toString} } """
      indexInto(indexType).source(src)
    }
  }
}
