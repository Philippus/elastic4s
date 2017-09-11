package com.sksamuel.elastic4s.delete

import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.elastic4s.{IndexAndType, IndexesAndTypes}

import scala.language.implicitConversions

trait DeleteApi {

  def deleteById(index: String, `type`: String, id: Any) = DeleteByIdDefinition(IndexAndType(index, `type`), id)
  def deleteByQuery(index: String, `type`: String, query: QueryDefinition) = DeleteByQueryDefinition(IndexAndType(index, `type`), query)

  def deleteIn(indexesAndTypes: IndexesAndTypes) = new DeleteByQueryExpectsQuery(indexesAndTypes)
  class DeleteByQueryExpectsQuery(indexesAndTypes: IndexesAndTypes) {
    def by(query: QueryDefinition): DeleteByQueryDefinition = DeleteByQueryDefinition(indexesAndTypes, query)
  }

  def delete(id: Any): DeleteByIdExpectsFrom = new DeleteByIdExpectsFrom(id)
  class DeleteByIdExpectsFrom(id: Any) {
    def from(indexAndType: IndexAndType): DeleteByIdDefinition = DeleteByIdDefinition(indexAndType, id)
  }
}
