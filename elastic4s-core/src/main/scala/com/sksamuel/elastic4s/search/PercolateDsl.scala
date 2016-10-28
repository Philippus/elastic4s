package com.sksamuel.elastic4s.search

import com.sksamuel.elastic4s.IndexAndType

trait PercolateDsl {

  def register(query: QueryDefinition) = new {
    def into(indexType: IndexAndType) = indexInto(indexType).source(query.builder.toString)
  }
}
