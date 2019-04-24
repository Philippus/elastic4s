package com.sksamuel.elastic4s.requests.delete

import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.elastic4s.{Index, Indexes}

import scala.language.implicitConversions

trait DeleteApi {

  def deleteById(index: Index, id: String) = DeleteByIdRequest(index.name, id)

  def deleteByQuery(index: Index, query: Query) =
    DeleteByQueryRequest(index.name, query)

  def deleteIn(indexesAndTypes: Indexes) = new DeleteByQueryExpectsQuery(indexesAndTypes)
  class DeleteByQueryExpectsQuery(indexesAndTypes: Indexes) {
    def by(query: Query): DeleteByQueryRequest = DeleteByQueryRequest(indexesAndTypes, query)
  }

  def delete(id: String): DeleteByIdExpectsFrom = new DeleteByIdExpectsFrom(id)
  class DeleteByIdExpectsFrom(id: String) {
    def from(index: Index): DeleteByIdRequest = DeleteByIdRequest(index, id)
  }
}
