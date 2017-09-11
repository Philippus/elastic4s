package com.sksamuel.elastic4s.update

import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.elastic4s.{IndexAndType, Indexes, IndexesAndTypes}

trait UpdateApi {

  def updateById(indexname: String, `type`: String, id: Any) = UpdateDefinition(indexname, id.toString)
  def updateByQuery(indexname: String, `type`: String, query: QueryDefinition) = UpdateByQueryDefinition(indexname, query)

  def update(id: Any): UpdateExpectsIn = new UpdateExpectsIn(id)
  class UpdateExpectsIn(id: Any) {
    def in(indexType: IndexAndType): UpdateDefinition = UpdateDefinition(indexType, id.toString)
  }

  def updateIn(indexes: Indexes): UpdateExpectsQuery = new UpdateExpectsQuery(indexes.toIndexesAndTypes)

  def updateIn(indexesAndTypes: IndexesAndTypes): UpdateExpectsQuery = new UpdateExpectsQuery(indexesAndTypes)

  class UpdateExpectsQuery(indexesAndTypes: IndexesAndTypes) {
    def query(query: QueryDefinition) = UpdateByQueryDefinition(indexesAndTypes, query)
  }
}


