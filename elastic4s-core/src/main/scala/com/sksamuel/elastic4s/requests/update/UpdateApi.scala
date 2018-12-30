package com.sksamuel.elastic4s.requests.update

import com.sksamuel.elastic4s.requests.searches.queries.Query
import com.sksamuel.elastic4s.{Index, IndexAndType, Indexes, IndexesAndTypes}

trait UpdateApi {

  def updateById(index: Index, `type`: String, id: String) = UpdateRequest(IndexAndType(index.name, `type`), id)
  def updateByQuery(index: Index, `type`: String, query: Query) =
    UpdateByQueryRequest(IndexAndType(index.name, `type`), query)

  def update(id: String): UpdateExpectsIn = new UpdateExpectsIn(id)
  class UpdateExpectsIn(id: String) {
    def in(indexType: IndexAndType): UpdateRequest = UpdateRequest(indexType, id)
  }

  def updateIn(indexes: Indexes): UpdateExpectsQuery = new UpdateExpectsQuery(indexes.toIndexesAndTypes)

  def updateIn(indexesAndTypes: IndexesAndTypes): UpdateExpectsQuery = new UpdateExpectsQuery(indexesAndTypes)

  class UpdateExpectsQuery(indexesAndTypes: IndexesAndTypes) {
    def query(query: Query) = UpdateByQueryRequest(indexesAndTypes, query)
  }
}
