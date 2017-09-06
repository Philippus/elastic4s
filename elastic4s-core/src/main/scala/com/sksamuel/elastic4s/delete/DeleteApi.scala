package com.sksamuel.elastic4s.delete

import com.sksamuel.elastic4s.searches.queries.QueryDefinition
import com.sksamuel.elastic4s.{IndexAndType, IndexAndTypes, IndexesAndTypes}

import scala.language.implicitConversions

trait DeleteApi {

  def deleteById(index: String, id: Any) = DeleteByIdDefinition(IndexAndTypes(index), id)
  def deleteByQuery(index: String, query: QueryDefinition) = DeleteByQueryDefinition(IndexesAndTypes(Seq(index), Nil), query)

  @deprecated("Use deleteByQuery(index, query) which only supports deleting by index, not by index and type. This is because in version 6 of Elasticsearch types are deprecated https://www.elastic.co/guide/en/elasticsearch/reference/master/removal-of-types.html.", "6.0.0")
  def deleteIn(indexesAndTypes: IndexesAndTypes) = new DeleteByQueryExpectsQuery(indexesAndTypes)
  class DeleteByQueryExpectsQuery(indexesAndTypes: IndexesAndTypes) {
    @deprecated("Use deleteById(index, id) which only supports deleting by index, not by index and type. This is because in version 6 of Elasticsearch types are deprecated https://www.elastic.co/guide/en/elasticsearch/reference/master/removal-of-types.html.", "6.0.0")
    def by(query: QueryDefinition): DeleteByQueryDefinition = DeleteByQueryDefinition(indexesAndTypes, query)
  }

  @deprecated("Use deleteById(index, id) which only supports deleting by index, not by index and type. This is because in version 6 of Elasticsearch types are deprecated https://www.elastic.co/guide/en/elasticsearch/reference/master/removal-of-types.html.", "6.0.0")
  def delete(id: Any): DeleteByIdExpectsFrom = new DeleteByIdExpectsFrom(id)
  class DeleteByIdExpectsFrom(id: Any) {
    @deprecated("Use deleteById(index, id) which only supports deleting by index, not by index and type. This is because in version 6 of Elasticsearch types are deprecated https://www.elastic.co/guide/en/elasticsearch/reference/master/removal-of-types.html.", "6.0.0")
    def from(indexAndType: IndexAndType): DeleteByIdDefinition = DeleteByIdDefinition(indexAndType.toIndexAndTypes, id)
  }
}
