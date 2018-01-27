package com.sksamuel.elastic4s.delete

import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.elastic4s.{Index, IndexAndType, IndexesAndTypes}

import scala.language.implicitConversions

trait DeleteApi {

  // the non type variants of these will be preferered over delete(id) and deleteIn(indextype) in v7.
  def deleteById(index: Index, `type`: String, id: String) = DeleteByIdDefinition(IndexAndType(index.name, `type`), id)
  def deleteByQuery(index: Index, `type`: String, query: QueryDefinition) =
    DeleteByQueryDefinition(IndexAndType(index.name, `type`), query)

  def deleteIn(indexesAndTypes: IndexesAndTypes) = new DeleteByQueryExpectsQuery(indexesAndTypes)
  class DeleteByQueryExpectsQuery(indexesAndTypes: IndexesAndTypes) {
    def by(query: QueryDefinition): DeleteByQueryDefinition = DeleteByQueryDefinition(indexesAndTypes, query)
  }

  def delete(id: String): DeleteByIdExpectsFrom = new DeleteByIdExpectsFrom(id)
  class DeleteByIdExpectsFrom(id: String) {
    def from(indexAndType: IndexAndType): DeleteByIdDefinition = DeleteByIdDefinition(indexAndType, id)
  }
}
