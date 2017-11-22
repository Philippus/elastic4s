package com.sksamuel.elastic4s.update

import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.elastic4s.{Index, IndexAndType, Indexes, IndexesAndTypes}

trait UpdateApi {

  def updateById(index: Index, `type`: String, id: String) = UpdateDefinition(index.name, id)
  def updateByQuery(index: Index, `type`: String, query: QueryDefinition) = UpdateByQueryDefinition(index.name, query)

  def update(id: String): UpdateExpectsIn = new UpdateExpectsIn(id)
  class UpdateExpectsIn(id: String) {
    def in(indexType: IndexAndType): UpdateDefinition = UpdateDefinition(indexType, id)
  }

  def updateIn(indexes: Indexes): UpdateExpectsQuery = new UpdateExpectsQuery(indexes.toIndexesAndTypes)

  def updateIn(indexesAndTypes: IndexesAndTypes): UpdateExpectsQuery = new UpdateExpectsQuery(indexesAndTypes)

  class UpdateExpectsQuery(indexesAndTypes: IndexesAndTypes) {
    def query(query: QueryDefinition) = UpdateByQueryDefinition(indexesAndTypes, query)
  }
}


