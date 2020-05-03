package com.sksamuel.elastic4s.requests.update

import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.elastic4s.{Index, Indexes}

trait UpdateApi {

  def updateById(index: Index, id: String): UpdateRequest = UpdateRequest(index.name, id)

  def updateByQuery(index: Index, query: Query): UpdateByQueryRequest = UpdateByQueryRequest(index.name, query)

  @deprecated("use updateById")
  def update(id: String): UpdateExpectsIn = new UpdateExpectsIn(id)
  class UpdateExpectsIn(id: String) {
    def in(index: Index): UpdateRequest = UpdateRequest(index, id)
  }

  @deprecated("use updateByQuery")
  def updateIn(indexes: Indexes): UpdateExpectsQuery = new UpdateExpectsQuery(indexes)

  class UpdateExpectsQuery(indexes: Indexes) {
    def query(query: Query): UpdateByQueryRequest = UpdateByQueryRequest(indexes, query)
  }
}
