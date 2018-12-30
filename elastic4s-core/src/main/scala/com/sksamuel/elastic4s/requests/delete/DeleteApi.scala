package com.sksamuel.elastic4s.requests.delete

import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.elastic4s.{Index, IndexAndType, IndexesAndTypes}

import scala.language.implicitConversions

trait DeleteApi {

  // the non type variants of these will be preferered over delete(id) and deleteIn(indextype) in v7.
  def deleteById(index: Index, `type`: String, id: String) = DeleteByIdRequest(IndexAndType(index.name, `type`), id)
  def deleteByQuery(index: Index, `type`: String, query: Query) =
    DeleteByQueryRequest(IndexAndType(index.name, `type`), query)

  def deleteIn(indexesAndTypes: IndexesAndTypes) = new DeleteByQueryExpectsQuery(indexesAndTypes)
  class DeleteByQueryExpectsQuery(indexesAndTypes: IndexesAndTypes) {
    def by(query: Query): DeleteByQueryRequest = DeleteByQueryRequest(indexesAndTypes, query)
  }

  def delete(id: String): DeleteByIdExpectsFrom = new DeleteByIdExpectsFrom(id)
  class DeleteByIdExpectsFrom(id: String) {
    def from(indexAndType: IndexAndType): DeleteByIdRequest = DeleteByIdRequest(indexAndType, id)
  }
}
