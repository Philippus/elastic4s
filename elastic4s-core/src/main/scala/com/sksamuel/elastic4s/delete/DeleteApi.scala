package com.sksamuel.elastic4s.delete

import com.sksamuel.elastic4s.searches.QueryDefinition
import com.sksamuel.elastic4s.{IndexAndType, Indexes}

import scala.language.implicitConversions

trait DeleteApi {

  def deleteIn(indexes: Indexes) = new DeleteByQueryExpectsQuery(indexes)
  class DeleteByQueryExpectsQuery(indexes: Indexes) {
    def by(query: QueryDefinition): DeleteByQueryDefinition = DeleteByQueryDefinition(indexes, query)
  }

  def delete(id: Any): DeleteByIdExpectsFrom = new DeleteByIdExpectsFrom(id)
  class DeleteByIdExpectsFrom(id: Any) {
    def from(indexAndType: IndexAndType): DeleteByIdDefinition = DeleteByIdDefinition(indexAndType, id)
  }
}
