package com.sksamuel.elastic4s.update

import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.elastic4s.{IndexAndTypes, Indexes, IndexesAndTypes}

trait UpdateApi {

  def updateById(indexname: String, id: Any) = UpdateDefinition(indexname, id.toString)

  def updateByQuery(indexname: String, query: QueryDefinition) = UpdateByQueryDefinition(indexname, query)

  @deprecated("Elasticsearch 6.0 has deprecated types and they will be removed in elasticsearch 7.0. Use updateById(index, id) which only supports updating by index, not by index and type. For more details see https://www.elastic.co/guide/en/elasticsearch/reference/master/removal-of-types.html.", "6.0.0")
  def update(id: Any): UpdateExpectsIn = new UpdateExpectsIn(id)
  class UpdateExpectsIn(id: Any) {
    def in(indexType: IndexAndTypes): UpdateDefinition = UpdateDefinition(indexType, id.toString)
  }

  @deprecated("Elasticsearch 6.0 has deprecated types and they will be removed in elasticsearch 7.0. Use updateByQuery(index, query) which only supports updating by index, not by index and type. For more details see https://www.elastic.co/guide/en/elasticsearch/reference/master/removal-of-types.html.", "6.0.0")
  def updateIn(indexes: Indexes): UpdateExpectsQuery = new UpdateExpectsQuery(indexes.toIndexesAndTypes)

  @deprecated("Elasticsearch 6.0 has deprecated types and they will be removed in elasticsearch 7.0. Use updateByQuery(index, query) which only supports updating by index, not by index and type. For more details see https://www.elastic.co/guide/en/elasticsearch/reference/master/removal-of-types.html.", "6.0.0")
  def updateIn(indexesAndTypes: IndexesAndTypes): UpdateExpectsQuery = new UpdateExpectsQuery(indexesAndTypes)

  class UpdateExpectsQuery(indexesAndTypes: IndexesAndTypes) {
    def query(query: QueryDefinition) = UpdateByQueryDefinition(indexesAndTypes, query)
  }
}


