package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.delete.{DeleteByIdRequest, DeleteByQueryRequest}
import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.elastic4s.{Index, Indexes}

trait DeleteApi {

  def deleteById(index: Index, id: String): DeleteByIdRequest = DeleteByIdRequest(index.name, id)
  def deleteByQuery(index: Index, query: Query): DeleteByQueryRequest = DeleteByQueryRequest(index.name, query)

  def deleteIn(indexesAndTypes: Indexes) = new DeleteByQueryExpectsQuery(indexesAndTypes)
  class DeleteByQueryExpectsQuery(indexesAndTypes: Indexes) {
    def by(query: Query): DeleteByQueryRequest = DeleteByQueryRequest(indexesAndTypes, query)
  }

  @deprecated("use deleteById(index, id)")
  def delete(id: String): DeleteByIdExpectsFrom = new DeleteByIdExpectsFrom(id)
  class DeleteByIdExpectsFrom(id: String) {
    def from(index: Index): DeleteByIdRequest = DeleteByIdRequest(index, id)
  }
}
