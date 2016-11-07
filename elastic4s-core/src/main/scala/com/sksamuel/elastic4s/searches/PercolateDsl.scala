package com.sksamuel.elastic4s.searches

import com.sksamuel.elastic4s.indexes.IndexDefinition
import com.sksamuel.elastic4s.{ElasticDsl, IndexAndType}

trait PercolateDsl {
  self: ElasticDsl =>

  def register(query: QueryDefinition) = new RegisterExpectsInto(query)
  class RegisterExpectsInto(query: QueryDefinition) {
    def into(indexType: IndexAndType, field: String = "query"): IndexDefinition = {
      val src = s""" { "$field" : ${query.builder.toString} } """
      indexInto(indexType).source(src)
    }
  }
}