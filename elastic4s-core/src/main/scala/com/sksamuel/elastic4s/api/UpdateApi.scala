package com.sksamuel.elastic4s.api

import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.elastic4s.requests.update.{UpdateByQueryRequest, UpdateRequest}
import com.sksamuel.elastic4s.{Index, Indexes}

trait UpdateApi {

  def updateById(index: Index, id: String): UpdateRequest = UpdateRequest(index.name, id)

  def updateByQuery(index: Index, query: Query): UpdateByQueryRequest = UpdateByQueryRequest(index.name, query)

  @deprecated("use updateById", "7.7")
  def update(id: String): UpdateExpectsIn = new UpdateExpectsIn(id)
  class UpdateExpectsIn(id: String) {
    def in(index: Index): UpdateRequest = UpdateRequest(index, id)
  }

  @deprecated("use updateByQuery", "7.7")
  def updateIn(indexes: Indexes): UpdateExpectsQuery = new UpdateExpectsQuery(indexes)

  class UpdateExpectsQuery(indexes: Indexes) {
    def query(query: Query): UpdateByQueryRequest = UpdateByQueryRequest(indexes, query)
  }
}
