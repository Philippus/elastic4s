package com.sksamuel.elastic4s.update

import com.sksamuel.elastic4s.searches.QueryDefinition
import com.sksamuel.elastic4s.{IndexAndTypes, Indexes}

trait UpdateApi {

  def update(id: Any): UpdateExpectsIn = new UpdateExpectsIn(id)
  class UpdateExpectsIn(id: Any) {
    def in(indexType: IndexAndTypes): UpdateDefinition = UpdateDefinition(indexType, id.toString)
  }

  def updateIn(indexes: Indexes): UpdateExpectsQuery = new UpdateExpectsQuery(indexes)
  class UpdateExpectsQuery(indexes: Indexes) {
    def query(query: QueryDefinition) = UpdateByQueryDefinition(indexes, query)
  }
}


