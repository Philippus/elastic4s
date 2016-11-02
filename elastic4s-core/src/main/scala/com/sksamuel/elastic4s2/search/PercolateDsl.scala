package com.sksamuel.elastic4s2.search

import com.sksamuel.elastic4s2.{ElasticDsl, IndexAndType}

trait PercolateDsl {
  self: ElasticDsl =>

  def register(query: QueryDefinition) = new {
    def into(indexType: IndexAndType) = indexInto(indexType).source(query.builder.toString)
  }
}
