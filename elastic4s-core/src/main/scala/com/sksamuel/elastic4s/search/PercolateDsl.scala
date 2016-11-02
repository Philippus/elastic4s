package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.{ElasticDsl, IndexAndType}

trait PercolateDsl {
  self: ElasticDsl =>

  def register(query: QueryDefinition) = new {
    def into(indexType: IndexAndType) = indexInto(indexType).source(query.builder.toString)
  }
}
