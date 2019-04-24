package com.sksamuel.elastic4s.requests.update

import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.elastic4s.{Index, Indexes}

trait UpdateApi {

  def updateById(index: Index, id: String) = UpdateRequest(index.name, id)
  def updateByQuery(index: Index, query: Query) =
    UpdateByQueryRequest(index.name, query)

  def update(id: String): UpdateExpectsIn = new UpdateExpectsIn(id)
  class UpdateExpectsIn(id: String) {
    def in(index: Index): UpdateRequest = UpdateRequest(index, id)
  }

  def updateIn(indexes: Indexes): UpdateExpectsQuery = new UpdateExpectsQuery(indexes)

  class UpdateExpectsQuery(indexes: Indexes) {
    def query(query: Query) = UpdateByQueryRequest(indexes, query)
  }
}
