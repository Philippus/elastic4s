package com.sksamuel.elastic4s.update

import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.elastic4s.{IndexAndTypes, Indexes, IndexesAndTypes}

trait UpdateApi {

  def update(id: Any): UpdateExpectsIn = new UpdateExpectsIn(id)
  class UpdateExpectsIn(id: Any) {
    def in(indexType: IndexAndTypes): UpdateDefinition = UpdateDefinition(indexType, id.toString)
  }

  def updateIn(indexes: Indexes): UpdateExpectsQuery = new UpdateExpectsQuery(indexes.toIndexesAndTypes)

  @deprecated("Use updateIn(index) which only supports updating by index, not by index and type. This is because in version 6 of Elasticsearch types are deprecated https://www.elastic.co/guide/en/elasticsearch/reference/master/removal-of-types.html.", "6.0.0")
  def updateIn(indexesAndTypes: IndexesAndTypes): UpdateExpectsQuery = new UpdateExpectsQuery(indexesAndTypes)

  class UpdateExpectsQuery(indexesAndTypes: IndexesAndTypes) {
    def query(query: QueryDefinition) = UpdateByQueryDefinition(indexesAndTypes, query)
  }
}


