package com.sksamuel.elastic4s.delete

import com.sksamuel.elastic4s.searches.{QueryDefinition, QueryDsl}
import com.sksamuel.elastic4s.{IndexAndTypes, Indexes}

import scala.language.implicitConversions

trait DeleteDsl extends QueryDsl {

  def deleteIn(indexes: Indexes) = new DeleteByQueryExpectsQuery(indexes)
  class DeleteByQueryExpectsQuery(indexes: Indexes) {
    def by(query: QueryDefinition): DeleteByQueryDefinition = DeleteByQueryDefinition(indexes, query)
  }

  def delete(id: Any): DeleteByIdExpectsFrom = new DeleteByIdExpectsFrom(id)
  class DeleteByIdExpectsFrom(id: Any) {
    def from(index: String): DeleteByIdDefinition = DeleteByIdDefinition(IndexAndTypes(index), id)
    def from(indexAndTypes: IndexAndTypes): DeleteByIdDefinition = DeleteByIdDefinition(indexAndTypes, id)
  }
}
